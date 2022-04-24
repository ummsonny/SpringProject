package jpabook.solo_jpashop.domain.item;

import jpabook.solo_jpashop.domain.Category;
import jpabook.solo_jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * 도메인 주도 설계시
     * 엔티티 자체만으로 해결할 수 있는 로직은 엔티티에다가
     * 여기 stockQuantity는 item안에 있으니
     * 뭔가 여기서 처리하는게 응집도가 있다.
     * 객체 지향적스럽다.
     * 상품 엔티티 개발(비지니스 로직 추가)편 -> 처음 or 4:06 or 5:44
     */

    //==비지니스 로직==//
    /**
     *  stock(재고) 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     *  stock(재고) 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity-quantity;
        if(restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
