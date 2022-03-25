package jpabook.solo_jpashop.service;


import jpabook.solo_jpashop.domain.item.Item;
import jpabook.solo_jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional //readOnly = false가 디폴트임
    public void saveItem(Item item){
        itemRepository.save(item);
    }


    /**
     * 영속성 컨텍스트가 자동 변경 update하니까
     */
    @Transactional //stockQuantity는 item내부에서 업데이트 한다. 그래서 여기 없다
    public void updateItem(Long id, String name, int price){
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
