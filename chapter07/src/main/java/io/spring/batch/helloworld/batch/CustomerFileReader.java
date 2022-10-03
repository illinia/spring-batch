package io.spring.batch.helloworld.batch;

import io.spring.batch.helloworld.domain.Customer;
import io.spring.batch.helloworld.domain.Transaction;
import org.springframework.batch.item.*;

import java.util.ArrayList;

public class CustomerFileReader implements ItemStreamReader<Customer> {

    private Object curItem = null;
    private ItemStreamReader<Object> delegate;

    public CustomerFileReader(ItemStreamReader<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Customer read() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }

        Customer item = (Customer) curItem;
        curItem = null;

        if (item != null) {

            item.setTransactions(new ArrayList<>());

            while(peek() instanceof Transaction) {
                item.getTransactions().add((Transaction) curItem);
                curItem = null;
            }
        }

        return item;
    }

    private Object peek() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }
        return curItem;
    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException {
        delegate.open(arg0);
    }


    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException {
        delegate.update(arg0);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
