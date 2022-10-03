package io.spring.batch.helloworld.batch;

import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CustomerByCityQueryProvider extends AbstractJpaQueryProvider {

    private String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public Query createQuery() {
        EntityManager manager = getEntityManager();

        Query query = manager.createQuery("select c from Customer c where c.city = :city");

        query.setParameter("city", cityName);

        return query;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cityName, "City name is required");
    }
}
