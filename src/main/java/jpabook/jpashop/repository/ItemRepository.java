package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {

    private final EntityManager em;

    public ItemRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        }
        else {
            em.merge(item);
        }
    }

    public Item find(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i").getResultList();
    }
}
