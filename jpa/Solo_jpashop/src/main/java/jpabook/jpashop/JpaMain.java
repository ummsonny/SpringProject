package jpabook.jpashop;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); //persistence.xml에 이름 설정해놈

        EntityManager entityManager = emf.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin(); // 트랜잭션은 db연결하는 것이라고 보면된다.

        try{

            tx.commit();

        }catch (Exception e){
            tx.rollback();
        }finally {
            entityManager.close();
        }


        emf.close();
    }
}

