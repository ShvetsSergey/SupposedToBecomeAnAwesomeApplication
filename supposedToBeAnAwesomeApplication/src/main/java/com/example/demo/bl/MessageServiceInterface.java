package com.example.demo.bl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dl.MessageEntity;

/**
 * 
 * Interface for MessageService class for operating on message table of
 * database.
 * 
 * @author serhii.shvets
 *
 */
@Service
public interface MessageServiceInterface {

	/**
	 * The method returning a list of all Message entity stored inside connected
	 * database.
	 * 
	 * @return List of MessageEntity objects. May return empty List which means that
	 *         there are no entities inside the database.
	 */
	public List<MessageEntity> findAll();

	/**
	 * The method for posting a new Message entity to the database. <br>
	 * 
	 * @param id   - value of m_id variable of new MessageEntity object.
	 * @param text - value of m_text variable of new MessageEntity object.
	 * @param grId - value of gr_id variable of new MessageEntity object.
	 * @return MessageEntity object same as posted in the database.
	 */
	public MessageEntity postMessage(int id, String text, int grId);

	/**
	 * The method for checking whether a message entity with provided ID exists.
	 * 
	 * @param id - ID of message entity.
	 * @return boolean true in case of message entity with such ID is existing,
	 *         otherwise boolean false.
	 */
	public boolean checkMessageIfExixting(int id);

	/**
	 * The method for deleting a message entity stored in the database.
	 * 
	 * @param id - ID of message entity to delete.
	 */
	public void deleteMessage(int id);

	/**
	 * The method for getting a MessageEntity object representing a message entity
	 * stored in the database.
	 * 
	 * @param id - id of MessageEntity wanted.
	 * @return MessageEntity object.
	 */
	public MessageEntity getMessageById(int id);

	/**
	 * The method for getting whole data from provided MessageEntity wrapped in
	 * readable text shape.
	 * 
	 * @param message - MessageEntity to operate.
	 * @return String sequence of whole data stored inside provided MessageEntity.
	 */
	public String getDataFromMessageEntity(MessageEntity message);

}
