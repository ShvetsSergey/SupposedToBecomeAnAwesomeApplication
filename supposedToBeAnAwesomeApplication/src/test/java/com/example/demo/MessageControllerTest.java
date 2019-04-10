package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.CannotCreateTransactionException;

import com.example.demo.bl.MessageService;
import com.example.demo.dl.MessageEntity;
import com.example.demo.dl.MessageRepository;

/**
 * IMPORTANT! -> Create MVC TESTS with mocking! Create positive and negative tests checking HTTP STATUSES.
 * In overall, there should be minimum 8 tests.
 * 
 * 
 * TID-123123
 */

import com.example.demo.sl.MessageController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageControllerTest {

	@Autowired
	private MessageController mesCont;

	@MockBean
	private MessageService mesService;

	@Autowired
	private MessageRepository messageRepository;

	MessageEntity mesEntity;

	@Before
	public void setup() {
		mesEntity = new MessageEntity();
	}

	@After
	public void clean() {
		messageRepository.deleteAll();
	}

	// GetMapping tests
	/**
	 * Case for testing MessageController.getMessageByID(int id) method behavior
	 * which is returning data from MessageEntity object by its provided ID
	 * parameter.<br>
	 * In this test case is tested case when provided ID of message which is
	 * existing.
	 * 
	 * <p>
	 * Expected response is data from MessageEntity object and HttpStatus.OK
	 */
	@Test
	public void getMappingMessageByIDPositiveTest() {

		mesEntity.setMesId(1L);
		mesEntity.setGrId(1L);
		mesEntity.setText("Text to inialize the object");
		mesEntity.setDate();

		String expectedDataFromMessageEntity = "";
		expectedDataFromMessageEntity += "Message with id " + mesEntity.getMesId() + ":\n";
		expectedDataFromMessageEntity += "Text of a message: \n";
		expectedDataFromMessageEntity += "\t" + mesEntity.getText() + "\n";
		expectedDataFromMessageEntity += "Date of posting message: \n";
		expectedDataFromMessageEntity += "\t " + mesEntity.getDate().toString() + "\n\n";

		Mockito.when(mesService.getDataFromMessageEntity(any(MessageEntity.class)))
				.thenReturn(expectedDataFromMessageEntity);
		Mockito.when(mesService.getMessageById(any(int.class))).thenReturn(mesEntity);

		ResponseEntity<String> actualResponseEntity = mesCont.getMessageById(mesEntity.getMesId().intValue());

		assertThat(actualResponseEntity.getBody()).isEqualTo(mesService.getDataFromMessageEntity(mesEntity));
		assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Case for testing MessageController.getMessageByID(int id) method behavior
	 * which is returning data from MessageEntity object by its provided ID
	 * parameter.<br>
	 * In this test case is tested case when provided ID of message which does not
	 * exists
	 * <p>
	 * Expected response is a message reporting that there is no Message with an ID
	 * provided and HttpStatus.NotFound.
	 */

	@Test
	public void getMappingUnexistingMessageByIDNegativeTest() {

		int idOfMessage = 1;
		String expectedResponseString = "Message with ID " + idOfMessage + " isnt exeisting yet";

		Mockito.when(mesService.getMessageById(idOfMessage)).thenThrow(new NullPointerException());
		Mockito.when(mesService.getDataFromMessageEntity(mesEntity)).thenReturn(null);

		ResponseEntity<String> actualResponseEntity = mesCont.getMessageById(idOfMessage);

		assertThat(actualResponseEntity.getBody()).contains(expectedResponseString);
		assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Case for testing the MessageController.getMessageById(int id) behavior in
	 * case if there are troubles with connection to the database.
	 * 
	 * <p>
	 * Expected response is a message reporting that checking the database
	 * connection needed and the HttpStatus.Forbidden
	 * 
	 */
	@Test
	public void getMappingTransactionExceptionGetMessageByIDNegativeTest() {

		Mockito.when(mesService.getMessageById(any(int.class)))
				.thenThrow((new CannotCreateTransactionException("Text of exception")));

		ResponseEntity<String> actualResponse = mesCont.getMessageById(1);

		assertThat(actualResponse.getBody()).describedAs("The actual response body is different from the expected one.")
				.contains("Check your database connection parameters.\n");
		assertThat(actualResponse.getBody())
				.describedAs("The actual text of exception is different from the expected one. ")
				.contains("Text of exception");
		assertThat(actualResponse.getStatusCode())
				.describedAs("The actual statuscode is different from the expected one.")
				.isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Case for testing showMessages() method which is returning all messages stored
	 * inside database.
	 * <p>
	 * Expected response is a bunch of messages and HttpStatus.Ok.
	 * 
	 */
	@Test
	public void getMappingMessagesPositiveTest() {

		String expectedResultString = "\tMessages stored inside DB:\n\n";
		List<MessageEntity> messages = new LinkedList<MessageEntity>();

		String[] messageTexts = { "Text of first message", "Text of second message", "Text of third message",
				"Text of fourth message", "Text of fifth message" };

		for (int i = 1; i < messageTexts.length; i++) {
			mesEntity.setMesId(new Long(i));
			mesEntity.setDate();
			mesEntity.setGrId(new Long(1));
			mesEntity.setText(messageTexts[i]);

			messages.add(mesEntity);

			expectedResultString += mesService.getDataFromMessageEntity(mesEntity);
		}

		Mockito.when(mesService.findAll()).thenReturn(messages);

		ResponseEntity<String> actualResponseFromTestedMethod = mesCont.showMessages();
		ResponseEntity<String> expectedResponseFromTestedMethod = new ResponseEntity<String>(expectedResultString,
				HttpStatus.OK);

		assertThat(actualResponseFromTestedMethod).isEqualTo(expectedResponseFromTestedMethod);

	}

	/**
	 * Case for testing MessageController.showMessages() method in case if it will
	 * get an empty MessageEntity List from MessageService, which is mean that there
	 * are no data stored inside database.
	 * <p>
	 * Expected response is message reporting that there are no data in database and
	 * HttpStatus.OK
	 * 
	 */

	@Test
	public void getMappingNoMessagesPositiveTest() {

		Mockito.when(mesService.findAll()).thenThrow(new IndexOutOfBoundsException());

		ResponseEntity<String> actualResponse = mesCont.showMessages();

		assertThat(actualResponse.getBody())
				.describedAs("The actual response body is different from the expected one. ")
				.contains("There are no messages stored inside the database.");
		assertThat(actualResponse.getStatusCode())
				.describedAs("The actual response HttpStatus is different from the expected one. ")
				.isEqualByComparingTo(HttpStatus.OK);
	}

	/**
	 * Case for testing MessageController.showMessages() method in case of troubles
	 * with database connection.
	 * <p>
	 * Expected response is text of thrown exception and HttpStatus.Forbidden
	 * 
	 */

	@Test
	public void getMappingCannotCreateTransactionExceptionNegativeTest() {

		Mockito.when(mesService.findAll()).thenThrow(new CannotCreateTransactionException("Text of exception"));

		ResponseEntity<String> actualResponse = mesCont.showMessages();

		assertThat(actualResponse.getBody())
				.describedAs("The actual text of exception is different from the expected one. ")
				.contains("Text of exception");
		assertThat(actualResponse.getStatusCode())
				.describedAs("The actual response HttpStatus is different from the expected one. ")
				.isEqualByComparingTo(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	/// PutMapping tests
	/**
	 * 
	 * Case for testing putMessage(int id, String newText) method which is updating
	 * an already MessageEntity object with the provided ID. <br>
	 * In this test case tested a case when such MessageEntity object is existing.
	 * <p>
	 * Expected response is a message reporting that MessageEntity was successfully
	 * updated containing data from updated MessageEntity and HttpStatus.OK
	 **/
	@Test
	public void putMappingMessagePositiveTest() {

		mesEntity.setMesId(1L);
		mesEntity.setGrId(1L);
		mesEntity.setText("Text to inialize the object");
		mesEntity.setDate();

		Mockito.when(mesService.checkMessageIfExixting(mesEntity.getMesId().intValue())).thenReturn(true);
		Mockito.when(mesService.postMessage(any(Integer.class), any(String.class), any(Integer.class)))
				.thenReturn(mesEntity);

		String newTextOfMessage = "Some new text to put into the message";

		mesEntity.setText(newTextOfMessage);

		String expectedDataFromMessageEntity = "";
		expectedDataFromMessageEntity += "Message with id " + mesEntity.getMesId() + ":\n";
		expectedDataFromMessageEntity += "Text of a message: \n";
		expectedDataFromMessageEntity += "\t" + mesEntity.getText() + "\n";
		expectedDataFromMessageEntity += "Date of posting message: \n";
		expectedDataFromMessageEntity += "\t " + mesEntity.getDate().toString() + "\n\n";
		Mockito.when(mesService.getDataFromMessageEntity(any(MessageEntity.class)))
				.thenReturn(expectedDataFromMessageEntity);

		mesCont.postMessage(mesEntity.getMesId().intValue(), mesEntity.getText());

		ResponseEntity<String> actualResponse = mesCont.putMessage(mesEntity.getMesId().intValue(), newTextOfMessage);

		assertThat(actualResponse.getBody()).contains(newTextOfMessage);
		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Case for testing putMessage(int id, String newText) method which is updating
	 * an already MessageEntity object with the provided ID.<br>
	 * In this test case tested a case when such MessageEntity object does not
	 * exists.
	 * <p>
	 * Expected response is message reporting that such message does not exists and
	 * the HttpStatusCode.NotFound
	 */
	@Test
	public void putMappingUnexistingMessageNegativeTest() {

		int idOfMessage = 1;
		String newTextOfMessage = "Some new text to update an old message object";

		Mockito.when(mesService.checkMessageIfExixting(any(Integer.class))).thenReturn(false);

		ResponseEntity<String> actualResponse = mesCont.putMessage(idOfMessage, newTextOfMessage);

		assertThat(actualResponse.getBody()).contains(
				"Unable to update a new message with ID " + idOfMessage + " since such message is not existing");
		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Case for testing putMessage(int id, String newText) method which is updating
	 * an already MessageEntity object with the provided ID. <br>
	 * In this test case tested a case when the new value of messageText of
	 * MessageEntity object in not provided.
	 * <p>
	 * Expected response the HttpStatusCode.OK
	 */
	@Test
	public void putMappingMessageWithNullTextPositiveTest() {

		mesEntity.setMesId(1L);
		mesEntity.setText("Some text");
		mesEntity.setGrId(1L);
		mesEntity.setDate();

		Mockito.when(mesService.checkMessageIfExixting(any(Integer.class))).thenReturn(true);
		Mockito.when(mesService.postMessage(any(int.class), any(String.class), any(int.class))).thenReturn(mesEntity);

		ResponseEntity<String> actualResponse = mesCont.putMessage(mesEntity.getMesId().intValue(), null);

		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	// PostMapping tests
	/**
	 * Case for testing a postMessage(int id, String text, int grId) method which is
	 * posting a new MessageEntity object to the database.<br>
	 * In this test case is tested case when whole provided data is proper.
	 * <p>
	 * Expected response is a message reporting that message was created
	 * successfully containing data from posted MessageEntity and
	 * HttpStatus.Created.
	 */
	@Test
	public void postMappingMessageWithAvailableIDPositiveTest() {
		String startTextOfMessage = "Some text to post into the message";
		Integer messageId = 1;

		mesEntity.setMesId(messageId.longValue());
		mesEntity.setText(startTextOfMessage);
		mesEntity.setGrId(1L);
		mesEntity.setDate();

		String expectedDataFromMessageEntity = "";
		expectedDataFromMessageEntity += "Message with id " + mesEntity.getMesId() + ":\n";
		expectedDataFromMessageEntity += "Text of a message: \n";
		expectedDataFromMessageEntity += "\t" + mesEntity.getText() + "\n";
		expectedDataFromMessageEntity += "Date of posting message: \n";
		expectedDataFromMessageEntity += "\t " + mesEntity.getDate().toString() + "\n\n";

		Mockito.when(mesService.checkMessageIfExixting(messageId)).thenReturn(false);
		Mockito.when(mesService.postMessage(messageId, startTextOfMessage, 1)).thenReturn(mesEntity);
		Mockito.when(mesService.getDataFromMessageEntity(mesEntity)).thenReturn(expectedDataFromMessageEntity);

		String expectedMessage = "Created new message with the following parameters:\n"
				+ mesService.getDataFromMessageEntity(mesEntity);

		ResponseEntity<String> actualResponseEntity = mesCont.postMessage(messageId, startTextOfMessage);

		assertThat(actualResponseEntity.getBody()).describedAs("Body is not equeal to the expected one")
				.isEqualTo(expectedMessage);
		assertThat(actualResponseEntity.getStatusCode()).describedAs("HttpStatus is not equeal to the expected one")
				.isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Case for testing a postMessage(int id, String text, int grId) method which is
	 * posting a new MessageEntity object to the database.<br>
	 * In this test case is tested case when provided value of text variable is
	 * equal to null.
	 * <p>
	 * Expected response HttpStatus.InternalServerError.
	 */
	@Test
	public void postMappingMessageWithNullTextNegativeTest() {
		Integer messageID = 1;

		Mockito.when(mesService.checkMessageIfExixting(messageID)).thenReturn(false);
		Mockito.when(mesService.postMessage(messageID, null, 1)).thenThrow(new NullPointerException());

		ResponseEntity<String> actualResponseEntity = mesCont.postMessage(messageID, null);

		assertThat(actualResponseEntity.getStatusCode()).describedAs("HttpStatus is not equeal to the expected one")
				.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * Case for testing a postMessage(int id, String text, int grId) method which is
	 * posting a new MessageEntity object to the database.<br>
	 * In this test case is tested case when provided ID of new MessageEntity object
	 * is already occupied by another MessageEntity object.
	 * <p>
	 * Expected response message reporting that there is no way to post message with
	 * provided ID and the HttpStatus.BadRequest.
	 */
	@Test
	public void postMappingMessageWithOccupiedIDNegativeTest() {

		String startTextOfMessage = "Some text to post into the message";
		int messageID = 1;

		String expectedErrorMessage = "Unable to post a new message with ID " + messageID
				+ " since message with provided ID is already exists.";

		Mockito.when(mesService.checkMessageIfExixting(messageID)).thenReturn(true);

		ResponseEntity<String> actualResponseEntity = mesCont.postMessage(messageID, startTextOfMessage);

		assertThat(actualResponseEntity.getBody()).describedAs("Body is not equeal to the expected one")
				.isEqualTo(expectedErrorMessage);
		assertThat(actualResponseEntity.getStatusCode()).describedAs("HttpStatus is not equeal to the expected one")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	// DeleteMapping tests
	/**
	 * Case to test the deleteMessage(int id) method which is designed to delete an
	 * already existing MessageEntity object from the database.<br>
	 * In this test case is tested case when MessageEntity with provided ID
	 * parameter is existing.
	 * <p>
	 * Expected response message reporting that the message with provided ID was
	 * deleted properly and the HttpStatus.OK.
	 */

	@Test
	public void deleteMessagePositiveTest() {

		int idOfMessage = 1;

		Mockito.when(mesService.checkMessageIfExixting(idOfMessage)).thenReturn(true);
		String expectedResponseBody = ("Message with id " + idOfMessage + " was deleted successfully :\n");

		ResponseEntity<String> actualResponse = mesCont.deleteMessage(idOfMessage);

		assertThat(actualResponse.getBody()).contains(expectedResponseBody);
		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	/**
	 * Case to test the deleteMessage(int id) method which is designed to delete an
	 * already existing MessageEntity object from the database.<br>
	 * In this test case is tested case when MessageEntity with provided ID
	 * parameter is not existing.
	 * <p>
	 * Expected response is warning reporting that the message with provided ID
	 * isn't exist and the HttpStatus.NotFound.
	 */
	@Test
	public void deleteUnexistingMessageNegativeTest() {
		int idOfMessage = 1;

		Mockito.when(mesService.checkMessageIfExixting(idOfMessage)).thenReturn(false);
		String expectedResponseBody = ("Unable to delete a message with ID " + idOfMessage
				+ " since such message is not existing.\n");

		ResponseEntity<String> actualResponse = mesCont.deleteMessage(idOfMessage);

		assertThat(actualResponse.getBody()).contains(expectedResponseBody);
		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * 
	 * Case to test the deleteMessage(int id) method which is designed to delete an
	 * already existing MessageEntity object from the database.<br>
	 * In this test case is tested case when there are troubles with the database
	 * connection.
	 * <p>
	 * Expected response is HttpStatus.Forbidden
	 */
	@Test
	public void deleteMessageThrowsCannotCreateConnectionExceptionNegativeTest() {
		int idOfMessage = 1;

		Mockito.when(mesService.checkMessageIfExixting(idOfMessage))
				.thenThrow(new CannotCreateTransactionException(null));

		ResponseEntity<String> actualResponse = mesCont.deleteMessage(idOfMessage);

		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

//	@Test
//	public void deleteMessageLoggerPositiveTest() {
//
//		int idOfMessage = 1;
//
//		TestLogger logger = TestLoggerFactory.getTestLogger(MessageController.class);
//
//		Mockito.when(mesService.checkMessageIfExixting(idOfMessage)).thenReturn(true);
//		// String expectedResponseBody = ("Message with id " + idOfMessage + " was
//		// deleted successfully :\n");
//
//		mesCont.deleteMessage(idOfMessage);
//
//		assertThat(logger.getLoggingEvents()).contains(uk.org.lidalia.slf4jtest.LoggingEvent.debug("delete"));
//
//	}

//		int idOfMessage = 1;
//
//		Mockito.when(mesService.checkMessageIfExixting(any(int.class))).thenReturn(true);
//		
//		mesCont.deleteMessage(idOfMessage);
//
//		Mockito.verify(log).debug("Message with id " + idOfMessage + " was deleted successfully");
//		
//	}

}
