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

import br.edu.projeto.model.Nationality;
import br.edu.projeto.util.DbUtil;

@Stateful
public class NationalityDAO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private DataSource dataSource;

    public List<Nationality> listAllNacionalities() {
        List<Nationality> nationalityList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM nacionalidades ORDER BY id ASC");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Nationality nationality = new Nationality();
                nationality.setIdNationality(resultSet.getInt("id"));
                nationality.setNationalityType(resultSet.getString("tipo_nacionalidade"));
                nationalityList.add(nationality);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeResultSet(resultSet);
            DbUtil.closePreparedStatement(preparedStatement);
            DbUtil.closeConnection(connection);
        }
        return nationalityList;
    }
}
