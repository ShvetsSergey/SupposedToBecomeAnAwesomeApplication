	public void postMappingMessageWithOccupiedIDNegativeTest() {
		String startTextOfMessage = "Some text to put into the message";
		int messageId = placeMessageInFreeSpace(startTextOfMessage);
		String expectedErrorMessage = "Unable to post a new message with ID " + messageId
				+ " since message with provided ID is already existing.";

		mesCont.postMessage(messageId, startTextOfMessage);
		ResponseEntity<String> actualResponseEntity = mesCont.postMessage(messageId, startTextOfMessage);

		assertThat(actualResponseEntity.getBody()).describedAs("Body is not equeal to the expected one")
				.isEqualTo(expectedErrorMessage);
		assertThat(actualResponseEntity.getStatusCode()).describedAs("HttpStatus is not equeal to the expected one")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	
	public void postMappingMessageWithOccupiedIDNegativeTest() {

		String startTextOfMessage = "Some text to put into the message";
		int messageID = 1;
		String expectedErrorMessage = "Unable to post a new message with ID " + messageID
				+ " since message with provided ID is already existing.";

		Mockito.when(mesService.checkMessageIfExixting(messageID)).thenReturn(true);

		ResponseEntity<String> actualResponseEntity = mesCont.postMessage(messageID, startTextOfMessage);

		assertThat(actualResponseEntity.getBody()).describedAs("Body is not equeal to the expected one")
				.isEqualTo(expectedErrorMessage);
		assertThat(actualResponseEntity.getStatusCode()).describedAs("HttpStatus is not equeal to the expected one")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}