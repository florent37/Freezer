package com.github.florent37.dao.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.florent37.dao.DAO;
import com.github.florent37.dao.model.User;
import com.github.florent37.dao.model.UserCursorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class UserDAO {

    public static UserDAOQueryBuilder selectWhere() {
        return new UserDAOQueryBuilder();
    }

    public static String create() {
        return "create table USER ( _id integer primary key autoincrement, age integer, name text not null );";
    }

    public static String update() {
        return "";
    }

    public static void add(User...users) {
        for(User user : users)
            add(user);
    }

    public static long add(User user) {
        ContentValues values = UserCursorHelper.getValues(user);

        long insertId = DAO.getInstance().open().getDatabase()
                .insert("USER", null, values);

        DAO.getInstance().close();
        return insertId;
    }

    public UserDAO delete(User user) {
        DAO.getInstance().open().getDatabase()
                .delete("USER", "_id = id", null);
        DAO.getInstance().close();
        return this;
    }

    public static class UserDAOQueryBuilder {

        StringBuilder queryBuilder;
        List<String> args;

        public UserDAOQueryBuilder() {
            this.queryBuilder = new StringBuilder();
            this.args = new ArrayList<>();
        }

        //region selectors
        public UserDAOQueryBuilder ageEquals(int age) {
            queryBuilder.append("age = ?");
            args.add(String.valueOf(age));
            return this;
        }

        public UserDAOQueryBuilder nameEquals(String name) {
            queryBuilder.append("name = ?");
            args.add(name);
            return this;
        }
        //endregion

        //region operators
        public UserDAOQueryBuilder or() {
            queryBuilder.append(" or ");
            return this;
        }

        public UserDAOQueryBuilder and() {
            queryBuilder.append(" and ");
            return this;
        }

        //public UserDAOQueryBuilder not() {
        //    return this;
        //}
        //endregion

        //region getters
        public List<User> asList() {
            return execute();
        }

        public User first() {
            List<User> users = asList();
            if (users.isEmpty())
                return null;
            else
                return users.get(0);
        }
        //endregion

        //region execute

        private String[] constructArgs() {
            return args.toArray(new String[args.size()]);
        }

        private String constructQuery() {
            if (queryBuilder.length() == 0)
                return "";
            return "where " + queryBuilder.toString();
        }

        private List<User> execute() {
            Cursor cursor = DAO.getInstance().open().getDatabase()
                    .rawQuery("select * from USER " + constructQuery(), constructArgs());

            List<User> users = UserCursorHelper.get(cursor);

            cursor.close();
            DAO.getInstance().close();

            return users;
        }
        //endregion
    }

}
