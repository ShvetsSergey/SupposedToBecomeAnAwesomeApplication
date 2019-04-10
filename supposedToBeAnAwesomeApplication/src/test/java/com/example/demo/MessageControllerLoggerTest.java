package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Before;
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
import com.example.demo.sl.MessageController;

/**
 * This class containing a methods designed to test the logger of
 * MessageController class.<br>
 * TID-123321
 * 
 * @author serhii.shvets
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageControllerLoggerTest {

	@Autowired
	private MessageController mesCont;

	@MockBean
	private MessageService mesService;

	@Autowired
	private MessageRepository messageRepository;

	/*
	 * Properties of MessageEntity object used in the testing.
	 */
	private MessageEntity mesEntity;
	private String expectedDataFromMessageEntity;
	String penultimateLineOfLog;
	private final Integer messageId = 1;
	private final String messageInitializationText = "Some text to post into the message";

	/**
	 * Actions performed before every test:<br>
	 * &#20 1. Creating a new MessageEntity object with specified properties<br>
	 * &#20 2. Reading the last line of ApplicationLog.log file<br>
	 * 
	 * 
	 */
	@Before
	public void setup() {
		mesEntity = new MessageEntity();

		mesEntity.setMesId(messageId.longValue());
		mesEntity.setText(messageInitializationText);
		mesEntity.setGrId(1L);
		mesEntity.setDate();

		expectedDataFromMessageEntity += "Message with id " + mesEntity.getMesId() + ":\n";
		expectedDataFromMessageEntity += "Text of a message: \n";
		expectedDataFromMessageEntity += "\t" + mesEntity.getText() + "\n";
		expectedDataFromMessageEntity += "Date of posting message: \n";
		expectedDataFromMessageEntity += "\t " + mesEntity.getDate().toString() + "\n\n";

		penultimateLineOfLog = getLastLineFromLog();
	}

	private String getLastLineFromLog() {
		String resultString = "";
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader("ApplicationLog.log"));

			String line;
			while ((line = input.readLine()) != null) {
				resultString = line;
			}

			input.close();
			return resultString;

		} catch (Exception e) {
			return ("Exception caught " + e.toString());
		}
	}

	/**
	 * Case for testing a logger of postMessage(int id, String text, int grId)
	 * method which is posting a new MessageEntity object to the database.<br>
	 * In this test case is tested case when whole provided data is proper.
	 * <p>
	 * Expected result is new line in log ApplicationLog.log file reporting that
	 * message with provided ID was created successfully.
	 */

	@Test
	public void postMessageSuccesfulLoggerPositiveTest() {

		Mockito.when(mesService.checkMessageIfExixting(messageId)).thenReturn(false);
		Mockito.when(mesService.postMessage(messageId, messageInitializationText, 1)).thenReturn(mesEntity);
		Mockito.when(mesService.getDataFromMessageEntity(mesEntity)).thenReturn(expectedDataFromMessageEntity);

		mesCont.postMessage(messageId, messageInitializationText);

		String lastLineOfLog = getLastLineFromLog();

		assertThat(lastLineOfLog).describedAs("New line to log wasnt added.").doesNotContain(penultimateLineOfLog);

		assertThat(lastLineOfLog).describedAs("Actual last line of log is different from the expected one.")
				.contains("Posted new message with id= " + messageId);
	}

	/**
	 * Case for testing a logger of postMessage(int id, String text, int grId)
	 * method which is posting a new MessageEntity object to the database.<br>
	 * In this test case is tested case when provided ID is occupied.
	 * <p>
	 * Expected result is new line in log ApplicationLog.log file reporting that
	 * there is no way to post a new message with provided ID since there is another
	 * message with the same ID.
	 */

	@Test
	public void postMessageUnsuccesfulLoggerPositiveTest() {

		Mockito.when(mesService.checkMessageIfExixting(any(int.class))).thenReturn(true);

		mesCont.postMessage(messageId, messageInitializationText);

		String lastLineOfLog = getLastLineFromLog();

		assertThat(lastLineOfLog).describedAs("New line to log wasnt added.").doesNotContain(penultimateLineOfLog);

		assertThat(lastLineOfLog).describedAs("Actual last line of log is different from the expected one.")
				.contains("Unable to post a new message with ID " + messageId
						+ " since message with provided ID is already exists.");
	}

	/**
	 * Case for testing a logger of postMessage(int id, String text, int grId)
	 * method which is posting a new MessageEntity object to the database.<br>
	 * In this test case tested case when there are troubles with database
	 * connection.
	 * <p>
	 * Expected result is new line in log ApplicationLog.log file reporting that
	 * there are troubles with database connection.
	 */
	@Test
	public void postMessageCannotCreateTransactionExceptionNegativeTest() {
		String textOfException = "Text of exception";

		CannotCreateTransactionException exception = new CannotCreateTransactionException(textOfException);

		Mockito.when(mesService.checkMessageIfExixting(any(int.class))).thenThrow(exception);
		mesCont.postMessage(messageId, messageInitializationText);

		String lastLineOfLog = getLastLineFromLog();

		assertThat(lastLineOfLog).describedAs("New line to log wasnt added.").doesNotContain(penultimateLineOfLog);

		assertThat(lastLineOfLog).describedAs("Actual last line of log is different from the expected one.")
				.contains("Wrong database connection parameters. " + exception.toString());
	}

}
