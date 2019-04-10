package com.example.demo.bl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dl.MessageEntity;
import com.example.demo.dl.MessageRepository;

/**
 * Implementation of MessageServiceInterface class for operating on message
 * table of database through MessageRepository class.
 * 
 * @author serhii.shvets
 *
 */
@Service
public class MessageService implements MessageServiceInterface {
	@Autowired
	private MessageRepository messageRepository;

	@Override
	public List<MessageEntity> findAll() {
		List<MessageEntity> messages = (List<MessageEntity>) messageRepository.findAll();
		return messages;
	}

	@Override
	public MessageEntity postMessage(int id, String text, int grId) {

		MessageEntity message = new MessageEntity();
		message.setMesId(new Long(id));
		message.setText(text);
		message.setGrId(new Long(grId));
		message.setDate();
		messageRepository.save(message);
		return message;
	}

	@Override
	public boolean checkMessageIfExixting(int id) {
		Long longId = new Long(id);
		return messageRepository.existsById(longId);
	}

	@Override
	public void deleteMessage(int id) {
		Long longId = new Long(id);
		messageRepository.deleteById(longId);
	}

	@Override
	public MessageEntity getMessageById(int id) {
		if (messageRepository.findById(new Long(id)).isPresent()) {
			return messageRepository.findById(new Long(id)).get();
		} else {
			return null;
		}
	}

	@Override
	public String getDataFromMessageEntity(MessageEntity message) {
		String resultString = "";
		resultString += "Message with id " + message.getMesId() + ":\n";
		resultString += "Text of a message: \n";
		resultString += "\t" + message.getText() + "\n";
		resultString += "Date of posting message: \n";
		resultString += "\t " + message.getDate().toString() + "\n\n";
		return resultString;
	}

}
