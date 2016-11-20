package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDao;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDao;

/**
 * Created by shadh on 20/11/2016.
 */

public class PersistentExpenseManager extends ExpenseManager {

    private Context context;

    public PersistentExpenseManager(Context context) {

        // point the constructor to setup function
        this.context = context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {

        // open an existing database, if a database already exists.
        // else create new database
        SQLiteDatabase database = context.openOrCreateDatabase("140263U",context.MODE_PRIVATE,null);

        AccountDAO accountDAO = new PersistentAccountDao(database);
        TransactionDAO transactionDAO = new PersistentTransactionDao(database);


        // create new tables in case of creating new database
        database.execSQL("CREATE TABLE IF NOT EXISTS Accounts(" +
                "account_no VARCHAR PRIMARY KEY," +
                "bank_name VARCHAR," +
                "account_holder_name VARCHAR," +
                "balance REAL" +
                ");");

        database.execSQL("CREATE TABLE IF NOT EXISTS Transactions(" +
                "transaction_ID INTEGER PRIMARY KEY," +
                "date DATE," +
                "account_no VARCHAR," +
                "expense_type INT(1)," +
                "amount REAL," +
                "FOREIGN KEY (account_no) REFERENCES Accounts(account_no)" +
                ");");


        // following two functions hold DAO instances in memory , until the program exists
        setAccountsDAO(accountDAO);
        setTransactionsDAO(transactionDAO);

    }
}
