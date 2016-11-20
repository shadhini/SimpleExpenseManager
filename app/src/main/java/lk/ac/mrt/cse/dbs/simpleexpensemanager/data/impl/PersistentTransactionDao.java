package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by shadh on 20/11/2016.
 */

public class PersistentTransactionDao implements TransactionDAO {
    private SQLiteDatabase database;

    // to store the database in the constructor in order to prevent opening it again and again
    public PersistentTransactionDao(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //prepare sql statement with variables to be hold for inserting records to Transactions table
        String sqlQuery = "INSERT INTO Transactions (date,account_no,expense_type,amount) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sqlQuery);

        //bind the values
        statement.bindLong(1, date.getTime());
        statement.bindString(2, accountNo);
        statement.bindLong(3, (expenseType == ExpenseType.EXPENSE) ? 0 : 1 );
        statement.bindDouble(4, amount);

        //execute the sql query
        statement.executeInsert();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        //initialize a list to store transactions
        List<Transaction> transactions = new ArrayList<Transaction>();

        //used Cursor as an iterator to loop through results
        Cursor results = database.rawQuery("SELECT * FROM Transactions",null);

        //loop through the results and add elements to the "transactions" list
        if(results.moveToFirst()){
            do{
                Transaction t = new Transaction(new Date(results.getLong(results.getColumnIndex("date"))),
                        results.getString(results.getColumnIndex("account_no")),
                        (results.getInt(results.getColumnIndex("expense_type"))== 0 ? ExpenseType.EXPENSE : ExpenseType.INCOME),
                        results.getDouble(results.getColumnIndex("amount")));

                transactions.add(t);
            } while (results.moveToNext());
        }

        //return the list of transactions
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        //initialize a list to store transactions
        List<Transaction> transactions = new ArrayList<Transaction>();

        Cursor results = database.rawQuery("SELECT * FROM Transactions LIMIT " + limit,null);

        //loop through the results and add elements to the "transactions" list
         if(results.moveToFirst()){
            do{
                Transaction t = new Transaction(new Date(results.getLong(results.getColumnIndex("date"))),
                        results.getString(results.getColumnIndex("account_no")),
                        (results.getInt(results.getColumnIndex("expense_type"))== 0 ? ExpenseType.EXPENSE : ExpenseType.INCOME),
                        results.getDouble(results.getColumnIndex("amount")));

                transactions.add(t);
            } while (results.moveToNext());
        }

        //return the list of transactions within the specified limit
        return transactions;
    }
}
