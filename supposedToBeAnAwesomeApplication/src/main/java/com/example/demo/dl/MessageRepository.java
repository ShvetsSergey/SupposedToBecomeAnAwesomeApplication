package com.example.demo.dl;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Class that represent a repository of message entities. Extends a
 * org.springframework.data.repository.CrudRepository.
 * 
 * @author serhii.shvets
 *
 */
@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, Long> {

}
