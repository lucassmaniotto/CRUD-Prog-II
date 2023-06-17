package br.edu.projeto.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.sql.DataSource;

import br.edu.projeto.model.Client;
import br.edu.projeto.util.DbUtil;

@Stateful
public class ClientDAO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private DataSource dataSource;

	public List<Client> listAll() {
	    List<Client> clientList = new ArrayList<Client>();
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    try {
	        connection = this.dataSource.getConnection();
	        preparedStatement = connection.prepareStatement("SELECT * FROM t_client ORDER BY cli_id ASC");
	        resultSet = preparedStatement.executeQuery();
	        while (resultSet.next()) {
	            Client client = new Client();
	            client.setIdClient(resultSet.getInt("cli_id"));
	            client.setCpf(resultSet.getString("cli_cpf"));
	            client.setName(resultSet.getString("cli_name"));
	            client.setSocialName(resultSet.getString("cli_social_name"));
	            client.setHeight(resultSet.getDouble("cli_height"));
	            client.setWeight(resultSet.getDouble("cli_weight"));
	            client.setGender(resultSet.getString("cli_gender"));
	            client.setAge(resultSet.getInt("cli_age"));
	            client.setEmail(resultSet.getString("cli_email"));
	            client.setCellphone(resultSet.getString("cli_cellphone"));
	            client.setAddress(resultSet.getString("cli_address"));
	            clientList.add(client);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DbUtil.closeResultSet(resultSet);
	        DbUtil.closePreparedStatement(preparedStatement);
	        DbUtil.closeConnection(connection);
	    }
	    return clientList;
	}

	public Client getClientById(int id) {
		Client client = new Client();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = this.dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT * FROM t_client WHERE cli_id = ?");
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				client.setCpf(resultSet.getString("cli_cpf"));
				client.setName(resultSet.getString("cli_name"));
				client.setSocialName(resultSet.getString("cli_social_name"));
				client.setHeight(resultSet.getDouble("cli_height"));
				client.setWeight(resultSet.getDouble("cli_weight"));
				client.setGender(resultSet.getString("cli_gender"));
				client.setAge(resultSet.getInt("cli_age"));
				client.setEmail(resultSet.getString("cli_email"));
				client.setCellphone(resultSet.getString("cli_cellphone"));
				client.setAddress(resultSet.getString("cli_address"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(resultSet);
			DbUtil.closePreparedStatement(preparedStatement);
			DbUtil.closeConnection(connection);
		}
		return client;
	}

	public Boolean insert(Client client) {
	    Boolean result = false;
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    try {
	        connection = this.dataSource.getConnection();
	        try {
	            preparedStatement = connection.prepareStatement(
	                "INSERT INTO t_client (cli_cpf, cli_name, cli_social_name, cli_height, cli_weight, cli_gender, cli_age, cli_email, cli_cellphone, cli_address, cli_nationality) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
	            );
	            preparedStatement.setString(1, client.getCpf());
	            preparedStatement.setString(2, client.getName());
	            preparedStatement.setString(3, client.getSocialName());
	            preparedStatement.setDouble(4, client.getHeight());
	            preparedStatement.setDouble(5, client.getWeight());
	            preparedStatement.setString(6, client.getGender());
	            preparedStatement.setInt(7, client.getAge());
	            preparedStatement.setString(8, client.getEmail());
	            preparedStatement.setString(9, client.getCellphone());
	            preparedStatement.setString(10, client.getAddress());
	            preparedStatement.setInt(11, client.getNationalityId());
	            preparedStatement.execute();
	            result = true;
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DbUtil.closePreparedStatement(preparedStatement);
	        DbUtil.closeConnection(connection);
	    }
	    return result;
	}


	public boolean update(Client client) {
		boolean result = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.dataSource.getConnection();
			try {
				preparedStatement = connection.prepareStatement(
					"UPDATE t_client SET cli_social_name = ?, cli_height = ?, cli_weight = ?, cli_gender = ?, cli_age = ?, cli_email = ?, cli_cellphone = ?, cli_address = ? WHERE cli_id = ?");
				preparedStatement.setString(1, client.getSocialName());
				preparedStatement.setDouble(2, client.getHeight());
				preparedStatement.setDouble(3, client.getWeight());
				preparedStatement.setString(4, client.getGender());
				preparedStatement.setInt(5, client.getAge());
				preparedStatement.setString(6, client.getEmail());
				preparedStatement.setString(7, client.getCellphone());
				preparedStatement.setString(8, client.getAddress());
				preparedStatement.setInt(9, client.getIdClient());
				preparedStatement.executeUpdate();
				result = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closePreparedStatement(preparedStatement);
			DbUtil.closeConnection(connection);
		}
		return result;
	}

	public Boolean delete(Client client) {
		Boolean result = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.dataSource.getConnection();
			try {
				preparedStatement = connection.prepareStatement("DELETE FROM t_client WHERE cli_id = ?");
				preparedStatement.setInt(1, client.getIdClient());
				preparedStatement.execute();
				result = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closePreparedStatement(preparedStatement);
			DbUtil.closeConnection(connection);
		}
		return result;
	}

    public Object getLastId() {
		Object lastId = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = this.dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT MAX(cli_id) FROM t_client");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				lastId = resultSet.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(resultSet);
			DbUtil.closePreparedStatement(preparedStatement);
			DbUtil.closeConnection(connection);
		}
		return lastId;
	}
}
