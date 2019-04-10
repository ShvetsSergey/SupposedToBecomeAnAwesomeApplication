package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.CoreMatchers.any; Not the package I`m looking for.
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.CannotCreateTransactionException;

import com.example.demo.bl.MessageService;
import com.example.demo.dl.MessageEntity;
import com.example.demo.dl.MessageRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {

	@MockBean
	private MessageRepository messageRepository;

	@Autowired
	private MessageService mesServ;

	/**
	 * 
	 * Case to test the findAll() method which is returning a List of all
	 * MessageEntity objects stored inside the database.<br>
	 * In this test case tested a case when there are MessageEntity objects inside
	 * the database.
	 * <p>
	 * Expected response is ArrayList of MessageEntity objects identical as was
	 * returned from MessageRepository.findAll() method.
	 */
	@Test
	public void findAllWhenMessagesExistingPositiveTest() {

		List<MessageEntity> expectedMessagesList = new ArrayList<MessageEntity>();
		MessageEntity mes = new MessageEntity();

		String[] messageTexts = { "Text of first message", "Text of second message", "Text of third message",
				"Text of fourth message", "Text of fifth message" };

		for (int i = 1; i < messageTexts.length; i++) {
			mes.setMesId(new Long(i));
			mes.setDate();
			mes.setGrId(new Long(1));
			mes.setText(messageTexts[i]);

			expectedMessagesList.add(mes);
		}

		Mockito.when(messageRepository.findAll()).thenReturn(expectedMessagesList);

		List<MessageEntity> actualMessagesList = (List<MessageEntity>) mesServ.findAll();

		for (int i = 0; i < expectedMessagesList.size(); i++) {
			assertThat(actualMessagesList.get(i))
					.describedAs("Actual object from the list is different from the expected one")
					.isEqualTo(expectedMessagesList.get(i));
		}

	}

	/**
	 * 
	 * Case to test the findAll() method which is returning a List of all
	 * MessageEntity objects stored inside the database.<br>
	 * In this test case tested a case when there are no MessageEntity objects
	 * inside the database.
	 * <p>
	 * Expected response is empty ArrayList of MessageEntity objects identical as
	 * was returned from MessageRepository.
	 */

	@Test
	public void findAllWhenNoMesagesExistingPositiveTest() {

		List<MessageEntity> expectedMessagesList = new ArrayList<MessageEntity>();

		Mockito.when(messageRepository.findAll()).thenReturn(expectedMessagesList);

		List<MessageEntity> actualMessagesList = (List<MessageEntity>) mesServ.findAll();

		assertThat(actualMessagesList).describedAs("Actual list of objects is different from the expected one.")
				.isEqualTo(expectedMessagesList);
	}

	/**
	 * 
	 * Case to test the findAll() method which is returning a List of all
	 * MessageEntity objects stored inside the database.<br>
	 * In this test case tested a case when there are troubles with connection to
	 * the database.
	 * <p>
	 * Expected response is thrown CannotCrreateTransactionException.
	 * 
	 * 
	 */
	@Test(expected = CannotCreateTransactionException.class)
	public void findAllWhenNoDataBaseConnectionNegativeTest() {

		Mockito.when(messageRepository.findAll()).thenThrow(new CannotCreateTransactionException("Text of exception"));

		mesServ.findAll();
	}

	/**
	 * 
	 * Case to test the findAll() method which is returning a List of all
	 * MessageEntity objects stored inside the database.<br>
	 * In this test case tested a case when messageService will return null object
	 * to the tested method.
	 * <p>
	 * Expected response is null object
	 */

	@Test
	public void findAllWhenMessageRepositoryReturnsNullNegativeTest() {

		List<MessageEntity> expectedMessagesList = null;

		Mockito.when(messageRepository.findAll()).thenReturn(null);

		List<MessageEntity> actualMessagesList = (List<MessageEntity>) mesServ.findAll();

		assertThat(actualMessagesList).describedAs("Actual list of objects is different from the expected one.")
				.isEqualTo(expectedMessagesList);
	}

	/**
	 * 
	 * Case to test the postMessage(int id, String text, int grId) method which is
	 * designed for posting a new MessageEntity object inside the database.<br>
	 * In this test case tested a case provided data is proper.
	 * <p>
	 * Expected response is empty ArrayList of MessageEntity objects identical as
	 * was returned from MessageRepository.
	 */
	@Test
	public void postMessagePositiveTest() {

		int messageID = 1;
		String messageText = "Some text";
		int messageGrID = 1;

		MessageEntity expectedMessage = new MessageEntity();

		expectedMessage.setMesId(new Long(messageID));
		expectedMessage.setText(messageText);
		expectedMessage.setGrId(new Long(messageGrID));
		expectedMessage.setDate();

		MessageEntity actualMessage = mesServ.postMessage(messageID, messageText, messageGrID);

		assertThat(actualMessage.getMesId())
				.describedAs(
						"Actual value of mesID variable of MessageEntity object is different from the expected one.")
				.isEqualTo(expectedMessage.getMesId());

		assertThat(actualMessage.getText()).describedAs(
				"Actual value of text String variable of MessageEntity object is different from the expected one.")
				.isEqualTo(expectedMessage.getText());

		assertThat(actualMessage.getDate())
				.describedAs(
						"Actual value of date variable of MessageEntity object is different from the expected one.")
				.isEqualTo(expectedMessage.getDate());

		assertThat(actualMessage.getGrId())
				.describedAs(
						"Actual value of grID variable of MessageEntity object is different from the expected one.")
				.isEqualTo(expectedMessage.getGrId());
	}

	/**
	 * Case to test the postMessage(int id, String text, int grId) method which is
	 * designed for posting a new MessageEntity object inside the database.<br>
	 * In this test case is tested a case if there are troubles with the database
	 * connection.
	 * <p>
	 * Expected result is thrown CannotCrreateTransactionException.
	 */
	@Test(expected = CannotCreateTransactionException.class)
	public void postMessageNoDatabaseConnectionNegativeTest() {
		Mockito.when(messageRepository.save(any(MessageEntity.class)))
				.thenThrow(new CannotCreateTransactionException("Text of exception"));

		mesServ.postMessage(1, "You shall not pass", 1);
	}

	/**
	 * 
	 * Case to test the checkIfExisting(int id) method which is designed to check
	 * whether an MessageEntity object with provided ID is existing<br>
	 * In this test case is tested a two cases:<br>
	 * 1. When such MessageEntity isn't exists,<br>
	 * 2. When such MessageEntity is existing.
	 * <p>
	 * Expected response is boolean.true in case if MessageEntity with provided ID
	 * exists or boolean.false in case if isn't.
	 */
	@Test
	public void checkMessageIfExistingPositiveTest() {

		int firstMessageID = 1;
		Mockito.when(messageRepository.existsById(new Long(firstMessageID))).thenReturn(true);
		boolean expectedFirstResponseFromTestedMethod = true;
		boolean firstResponseFromTestedMethod = mesServ.checkMessageIfExixting(firstMessageID);

		int secondMessageID = 2;
		Mockito.when(messageRepository.existsById(new Long(secondMessageID))).thenReturn(false);
		boolean expectedSecondResponseFromTestedMethod = false;
		boolean secondResponseFromTestedMethod = mesServ.checkMessageIfExixting(secondMessageID);

		assertThat(firstResponseFromTestedMethod)
				.describedAs("Actual response from method is different from the expected boolean.true value.")
				.isEqualTo(expectedFirstResponseFromTestedMethod);

		assertThat(secondResponseFromTestedMethod)
				.describedAs("Actual response from method is different from the expected boolean.false value.")
				.isEqualTo(expectedSecondResponseFromTestedMethod);
	}

	/**
	 * 
	 * Case to test the MessageService.deleteById(id) method which is designed to
	 * call the MessageRepository.deleteById(Long id) which is deleting a
	 * MessageEntity stored inside the database by the provided ID<br>
	 * This test case verifying if tested method is calling
	 * MessageRepository.deleteById properly.
	 * <p>
	 * Expected behavior is calling MessageRepository.deleteById(id) by
	 * MessageService.deleteById(id) with the same ID
	 */

	@Test
	public void deleteByIDPositiveTest() {
		int messageID = 1;

		mesServ.deleteMessage(messageID);

		Mockito.verify(messageRepository).deleteById(new Long(messageID));
	}

	/**
	 * Case to test the MessageService.getMessageById(id) method which is designed to get MessageEntity object from the database.
	 * In
	 * <p>
	 * Expected response is MessageEntity class object indent as stored inside
	 * mocked MessageRepository
	 */
	@Test
	public void getMessageByIdPositiveTest() {

		Integer messageID = 1;
		String messageText = "Some text";
		Long messageGrID = 1L;

		MessageEntity expectedMessage = new MessageEntity();

		expectedMessage.setMesId(messageID.longValue());
		expectedMessage.setText(messageText);
		expectedMessage.setGrId(messageGrID);
		expectedMessage.setDate();

		Optional<MessageEntity> of = Optional.of(expectedMessage);

		Mockito.when(messageRepository.findById(messageID.longValue())).thenReturn(of);

		assertThat(mesServ.getMessageById(messageID))
				.describedAs("Actual object returned from method is different from the expected one.")
				.isEqualTo(expectedMessage);
	}

	/**
	 * Case to test the MessageService.getMessageById(id) method returning null
	 * <p>
	 * Expected response is null such MessageEntity with provided ID isn't exist
	 */
	@Test
	public void getMessageByIdOfUnexistingMessagePositiveTest() {

		Integer messageID = 1;

		Optional<MessageEntity> of = Optional.empty();

		Mockito.when(messageRepository.findById(messageID.longValue())).thenReturn(of);

		assertThat(mesServ.getMessageById(messageID))
				.describedAs("Actual object returned from method is different from the expected one.").isEqualTo(null);
	}

	/**
	 * Case to test the MessageService.getDataFromMessageEntity(MessageEntity)
	 * method returning proper String
	 * <p>
	 * Expected response is whole data stored inside MessageEntity object wrapped
	 * with annotations and line breaks
	 * 
	 */
	@Test
	public void getDataFromMessageEntityPositiveTest() {

		int messageID = 1;
		String messageText = "Some text";
		int messageGrID = 1;

		MessageEntity message = new MessageEntity();

		message.setMesId(new Long(messageID));
		message.setText(messageText);
		message.setGrId(new Long(messageGrID));
		message.setDate();

		String expectedResponseString = "";

		expectedResponseString += "Message with id " + message.getMesId() + ":\n";
		expectedResponseString += "Text of a message: \n";
		expectedResponseString += "\t" + message.getText() + "\n";
		expectedResponseString += "Date of posting message: \n";
		expectedResponseString += "\t " + message.getDate().toString() + "\n\n";

		String actualResponseString = mesServ.getDataFromMessageEntity(message);

		assertThat(actualResponseString)
				.describedAs("Actual response String returned from tested method is different from the expected one.")
				.isEqualTo(expectedResponseString);
	}

	/**
	 * Case to test the MessageService.getDataFromMessageEntity(MesageEntity
	 * message) method behavior in case of putting null object inside the method.
	 * <p>
	 * Expected response is thrown NullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void getDataFromNullMessageEntityObjectNegativeTest() {

		MessageEntity message = null;

		mesServ.getDataFromMessageEntity(message);
	}

}
