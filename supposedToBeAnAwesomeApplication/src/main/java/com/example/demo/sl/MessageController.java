package com.example.demo.sl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bl.MessageService;
import com.example.demo.dl.MessageEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * Rest controller responsible for handling next list of Http requests:
 * <P>
 * <i>Messages&#47</i> - returns a list of all MessageEntity objects stored
 * inside the database.<br>
 * This request is handled by showMessages() method.
 * <p>
 * <i>Message&#47id</i> - returns data stored in MessageEntity object with
 * provided ID.<br>
 * This request is handled by getMessageById(int id) method
 * <p>
 * <i>deleteMessage&#47id</i> - request which provides to delete the
 * MessageEntity stored inside the database with provided ID.<br>
 * This request is handled by deleteMessage(int id) method.
 * <p>
 * <i>postMessage&#47id&#47text</i> - request which provides to posting a new
 * MessageEntity object with provided parameters inside the database.<br>
 * This request is handled by postMessage(int id, String text) method.
 * <p>
 * <i>putMessage&#47id&#47newText</i> - request which provides to put an
 * existing MessageEntity by provided ID with new text value.<br>
 * This request is handled by putMessage(int id, String text) method.
 * 
 * @author serhii.shvets
 *
 */
@Slf4j // dodaj logowanie na poziomach error, debug, info
@RestController

public class MessageController implements MessagerControllerInterface {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MessageService messageService;

	@Override
	public ResponseEntity<String> showMessages() {

		try {
			String resultString = "\tMessages stored inside DB:\n\n";
			List<MessageEntity> messages = (List<MessageEntity>) messageService.findAll();
			messages.get(0);
			for (int i = 0; i < messages.size(); i++) {
				resultString += messageService.getDataFromMessageEntity(messages.get(i));
			}
			log.debug("Obtained messages count: {}", messages.size());
			return new ResponseEntity<String>(resultString, HttpStatus.OK);
		} catch (CannotCreateTransactionException e) {
			log.error("Check database connection parameters" + e.toString());
			return new ResponseEntity<String>("Check your database connection parameters.\n" + e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IndexOutOfBoundsException e) {
			log.debug("There are no messages stored inside the database");
			String resultString = "There are no messages stored inside the database.\n";
			return new ResponseEntity<String>(resultString, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception caught" + e.toString());
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> postMessage(int id, String text) {
		try {
			if (!messageService.checkMessageIfExixting(id)) {
				String resultString = "Created new message with the following parameters:\n";
				MessageEntity message = messageService.postMessage(id, text, 1);
				log.debug("Posted new message with id= " + message.getMesId());
				return new ResponseEntity<String>(resultString + messageService.getDataFromMessageEntity(message),
						HttpStatus.CREATED);

			} else {
				log.debug("Unable to post a new message with ID " + id
						+ " since message with provided ID is already exists.");
				return new ResponseEntity<String>("Unable to post a new message with ID " + id
						+ " since message with provided ID is already exists.", HttpStatus.BAD_REQUEST);
			}

		} catch (CannotCreateTransactionException e) {
			log.error("Wrong database connection parameters. " + e.toString());
			return new ResponseEntity<String>("Check your database connection parameters.\n" + e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Exception caught" + e.toString());
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> putMessage(int id, String text) {
		try {
			if (messageService.checkMessageIfExixting(id)) {
				MessageEntity message = messageService.postMessage(id, text, 1);
				log.debug("Message was updated successfully.");
				return new ResponseEntity<String>(
						"Message updated successfully :\n" + messageService.getDataFromMessageEntity(message),
						HttpStatus.OK);

			} else {
				log.debug("Unable to update a new message with ID " + id + " since such message is not existing");
				return new ResponseEntity<String>(
						"Unable to update a new message with ID " + id + " since such message is not existing",
						HttpStatus.NOT_FOUND);
			}

		} catch (CannotCreateTransactionException e) {
			log.error("Wrong database connection parameters. " + e.toString());
			return new ResponseEntity<String>("Check your database connection parameters.\n" + e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Exception caught. " + e.toString());
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> deleteMessage(int id) {
		try {
			if (messageService.checkMessageIfExixting(id)) {
				messageService.deleteMessage(id);
				log.debug("Message with id " + id + " was deleted successfully");
				return new ResponseEntity<String>("Message with id " + id + " was deleted successfully :\n",
						HttpStatus.OK);

			} else {
				log.debug("Unable to delete a message with ID " + id + " since such message is not existing.");
				return new ResponseEntity<String>(
						"Unable to delete a message with ID " + id + " since such message is not existing.\n",
						HttpStatus.NOT_FOUND);
			}

		} catch (CannotCreateTransactionException e) {
			return new ResponseEntity<String>("Check your database connection parameters.\n" + e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Exception caught. " + e.toString());
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getMessageById(int id) {
		try {
			return new ResponseEntity<String>(
					messageService.getDataFromMessageEntity(messageService.getMessageById(id)), HttpStatus.OK);
		} catch (NullPointerException e) {
			log.debug("Message with ID " + id + " isnt exeisting yet");
			return new ResponseEntity<String>("Message with ID " + id + " isnt exeisting yet", HttpStatus.NOT_FOUND);
		} catch (CannotCreateTransactionException e) {
			log.error("Wrong database connection parameters. " + e.toString());
			return new ResponseEntity<String>("Check your database connection parameters.\n" + e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Exception caught. " + e.toString());
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
