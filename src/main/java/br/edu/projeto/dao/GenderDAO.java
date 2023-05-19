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

import br.edu.projeto.model.Gender;
import br.edu.projeto.util.DbUtil;

@Stateful
public class GenderDAO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private DataSource dataSource;

    public List<Gender> getAllGenders() {
        List<Gender> genderOptions = new ArrayList<Gender>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM t_gender_option ORDER BY gen_id ASC");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Gender gender = new Gender();
                gender.setLabel(resultSet.getString("gen_label"));
                gender.setDescription(resultSet.getString("gen_value"));
                genderOptions.add(gender);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeResultSet(resultSet);
            DbUtil.closePreparedStatement(preparedStatement);
            DbUtil.closeConnection(connection);
        }
        return genderOptions;
    }
}