package com.example.demo.sl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Interface for MessageController designed to handle a Http requests directed
 * to the application.
 * 
 * @author serhii.shvets
 *
 */
@RestController
public interface MessagerControllerInterface {

	/**
	 * Request handler for accessing a message entities stored in the database.
	 * 
	 * @return ResponseEntity of String type with data of all message entity stored
	 *         inside the database.
	 */
	@GetMapping("/Messages")
	public ResponseEntity<String> showMessages();

	/**
	 * Request handler for posting a new message entity to the database.
	 * 
	 * @param id   - ID of posted message entity. Accepts int data type.
	 * @param text - Text of posted message entity. Accepts String data type.
	 * @return ResponseEntity of String type.
	 */
	@PostMapping("/postMessage/{id}/{text}")
	public ResponseEntity<String> postMessage(@PathVariable(name = "id", required = true) int id,
			@PathVariable(name = "text", required = false) String text);

	/**
	 * 
	 * Request handler for putting a message entity with provided ID.
	 * 
	 * @param id   - ID of puted message entity. Accepts int data type.
	 * @param text - New text of puted message entity. Accepts String data type.
	 * @return ResponseEntity of String type.
	 */
	@PutMapping("/putMessage/{id}/{text}")
	public ResponseEntity<String> putMessage(@PathVariable(name = "id", required = true) int id,
			@PathVariable(name = "text", required = false) String text);

	/**
	 * Request handler for deleting a message entity with provided ID.
	 * 
	 * @param id - ID of message entity to delete. Accepts int data type.
	 * @return ResponseEntity of String type.
	 */
	@DeleteMapping("/deleteMessage/{id}")
	public ResponseEntity<String> deleteMessage(@PathVariable(name = "id", required = true) int id);

	/**
	 * @param id - ID of wanted message entity. Accepts int data type.
	 * @return ResponseEntity of String type.
	 */
	@GetMapping("/Message/{id}")
	public ResponseEntity<String> getMessageById(@PathVariable(name = "id", required = true) int id);

}
