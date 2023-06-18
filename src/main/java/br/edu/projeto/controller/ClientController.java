package br.edu.projeto.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.edu.projeto.dao.ClientDAO;
import br.edu.projeto.dao.NationalityDAO;
import br.edu.projeto.model.Client;
import br.edu.projeto.model.Nationality;

@ViewScoped
@Named
public class ClientController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContext facesContext;

	@Inject
	private ClientDAO clientDAO;

	private Client client;

	private List<Client> clientList;

	private Boolean renderNewRegister;

	private String filterName;

	private String filterGender;

	private List<Client> filteredClients;
	
	@Inject
    private NationalityDAO nationalityDAO;
    private List<Nationality> nationalityList;


    @PostConstruct
    public void init() {
        this.clientList = clientDAO.listAll();
        this.nationalityList = nationalityDAO.listAllNacionalities();
    }

	public void newRegister() {
		this.setClient(new Client());
		this.setRenderNewRegister(true);
	}

	public void updateRegister() {
		this.setRenderNewRegister(false);
	}

	public void remove() {
		if (this.clientDAO.delete(this.client)) {
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente removido com sucesso!", null));
			this.clientList.remove(this.client);
		} else
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao remover cliente!", null));
		this.client = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-camisetas");
	}

	public void saveNew() {
		if (!isValidCpf(this.client.getCpf())) {
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CPF deve conter apenas números e estar no formato xxx.xxx.xxx-xx!", null));
			return;
		}

		if (!isValidName(this.client.getName())) {
			this.facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "O nome do cliente deve conter apenas letras!", null));
			return;
		}
		
		if (!validateClientData()) {
			return;
		}
		if (this.clientDAO.insert(this.client)) {
			this.getClientList().add(this.client);
			PrimeFaces.current().executeScript("PF('camisetaDialog').hide()");
			PrimeFaces.current().ajax().update("form:dt-camiseta");
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente cadastrado com sucesso!", null));
		} else
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao cadastrar cliente!", null));
		PrimeFaces.current().ajax().update("form:messages");
		this.setClientList(clientDAO.listAll());
	}

	public void saveUpdate() {
		if (!validateClientData()) {
			return;
		}
		if (this.clientDAO.update(this.client)) {
			PrimeFaces.current().executeScript("PF('camisetaDialog').hide()");
			PrimeFaces.current().ajax().update("form:dt-camiseta");
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente atualizado com sucesso!", null));
		} else
			this.facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao atualizar cliente!", null));
		this.setClientList(clientDAO.listAll());
		PrimeFaces.current().ajax().update("form:messages");
	}

	public void getLastId() {
		this.client.setIdClient((int) this.clientDAO.getLastId());
	}

	public List<Client> getFilteredClients() {
		if (filterName == null || filterName.isEmpty()) {
			filteredClients = clientList;
		} else {
			filteredClients = new ArrayList<>();
			for (Client client : clientList) {
				if (client.getName().toLowerCase().contains(filterName.toLowerCase())) {
					filteredClients.add(client);
				}
			}
		}
		
		if (filterGender != null && !filterGender.isEmpty()) {
			List<Client> filteredByGender = new ArrayList<>();
			for (Client client : filteredClients) {
				if (client.getGender().equalsIgnoreCase(filterGender)) {
					filteredByGender.add(client);
				}
			}
			filteredClients = filteredByGender;
		}
		
		return filteredClients;
	}
	
	// Validações
	private boolean validateClientData() {
		if (!validateHeight(Double.toString(this.client.getHeight()))) {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Altura inválida. A altura deve ser um valor numérico maior que zero e entre 1 a 2.5 metros.", null));
			return false;
		}
		if (!validateWeight(Double.toString(this.client.getWeight()))) {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Peso inválido. O peso deve ser um valor numérico maior que zero e menor ou igual a 250 kg.", null));
			return false;
		}
		if (!validateAge(Integer.toString(this.client.getAge()))) {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Idade inválida. A idade deve ser um valor inteiro maior que zero.", null));
			return false;
		}
		
		if (!validateCellphoneFormat(this.client.getCellphone())) {
			this.facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "O telefone deve estar no formato (xx)xxxxx-xxxx!", null));
			return false;
		}
		return true;
	}	

	private boolean isValidName(String name) {
		return name.matches("[a-zA-Z\\s]+");
	}

	private boolean isValidCpf(String cpf) {
		if (cpf.length() != 14) {
			return false;
		}
		Pattern pattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
		Matcher matcher = pattern.matcher(cpf);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	private boolean validateHeight(String height) {
		try {
			double heightValue = Double.parseDouble(height.replace(',', '.'));
			return heightValue > 0 && heightValue <= 2.5 && Math.abs(heightValue - Math.floor(heightValue * 100) / 100) < 0.01;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean validateWeight(String weight) {
		try {
			double weightValue = Double.parseDouble(weight.replace(",", "."));
			return weightValue > 0 && weightValue <= 250;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean validateAge(String age) {
		try {
			int ageValue = Integer.parseInt(age);
			return ageValue > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean validateCellphoneFormat(String cellphone) {
		return cellphone.matches("\\(\\d{2}\\)\\d{5}-\\d{4}");
	}

	public String getNationalityText(int nationalityId) {
		if (nationalityId == 1) {
			return "Brasileiro nato";
		} else if (nationalityId == 2) {
			return "Brasileiro naturalizado";
		} else if (nationalityId == 3) {
			return "Dupla ou múltipla nacionalidade";
		} else if (nationalityId == 4) {
			return "Perda da nacionalidade";
		} else if (nationalityId == 5) {
			return "Reaquisição da nacionalidade";
		} else if (nationalityId == 6) {
			return "Estrangeiro";
		} else {
			return "Nacionalidade desconhecida";
		}
	}
	

	// GETs e SETs
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}

	public Boolean getRenderNewRegister() {
		return renderNewRegister;
	}

	public void setRenderNewRegister(Boolean renderNewRegister) {
		this.renderNewRegister = renderNewRegister;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getFilterGender() {
		return filterGender;
	}

	public void setFilterGender(String filterGender) {
		this.filterGender = filterGender;
	}
	
	public List<Nationality> getNationalityList() {
        return nationalityList;
    }

    public void setNationalityList(List<Nationality> nationalityList) {
        this.nationalityList = nationalityList;
    }
}
