package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by shadh on 20/11/2016.
 */

public class PersistentAccountDao implements AccountDAO {
    private SQLiteDatabase database;

    // to store the database in the constructor in order to prevent opening it again and again
    public PersistentAccountDao(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public List<String> getAccountNumbersList() {
        //initialize a list to store account numbers
        List<String> accountNumberList = new ArrayList<String>() ;

        //used Cursor as an iterator to loop through results
        Cursor results = database.rawQuery("SELECT account_no FROM Accounts", null);

        //loop through the results and add elements to the accountNumberList
        if(results.moveToFirst()){
            do{
                accountNumberList.add(results.getString(results.getColumnIndex("account_no")));
            } while (results.moveToNext());
        }

        //return the list of account numbers
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        //initialize a list to store accounts
        List<Account> accounts = new ArrayList<>();

        Cursor results = database.rawQuery("SELECT * FROM Accounts",null);

        //loop through the results and add elements to the "accounts" list
        if(results.moveToFirst()){
            do{
                Account account  = new Account(results.getString(results.getColumnIndex("account_no")),
                        results.getString(results.getColumnIndex("bank_name")),
                        results.getString(results.getColumnIndex("account_holder_name")),
                        results.getDouble(results.getColumnIndex("balance")));

                accounts.add(account);
            } while (results.moveToNext());
        }

        //return the list of accounts
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        //initialize an Account object to store account
        Account account = null;

        Cursor results=database.rawQuery("SELECT * FROM Accounts WHERE account_no = " + accountNo,null );

        //loop through the results
        if(results.moveToFirst()){
            do{
                account = new Account(results.getString(results.getColumnIndex("account_no")),
                        results.getString(results.getColumnIndex("bank_name")),
                        results.getString(results.getColumnIndex("account_holder_name")),
                        results.getDouble(results.getColumnIndex("balance")));
            } while (results.moveToNext());
        }


        return  account;
    }

    @Override
    public void addAccount(Account account) {
        //prepare sql statement with variables to be hold for inserting records to Accounts table
        String sqlQuery = "INSERT INTO Accounts (account_no, bank_name, account_holder_name,balance) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sqlQuery);

        //bind the values
        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());

        //execute sql query
        statement.executeInsert();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        //prepare sql statement with variables to be hold for deleting record from Accounts table
        String sqlQuery = "DELETE FROM Accounts WHERE account_no = ? ";
        SQLiteStatement statement = database.compileStatement(sqlQuery);

        //bind the values
        statement.bindString(1, accountNo);

        //execute sql query
        statement.executeUpdateDelete();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        //prepare sql statement with variables to be hold for updating records of Accounts table
        String sqlQuery = "UPDATE Accounts SET balance = balance + ? ";
        SQLiteStatement statement = database.compileStatement(sqlQuery);

        //bind the values
        if (expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1, -amount);
        }else{
            statement.bindDouble(1, amount);
        }

        //execute sql query
        statement.executeUpdateDelete();

    }
}
