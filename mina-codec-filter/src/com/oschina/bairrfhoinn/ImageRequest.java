package com.oschina.bairrfhoinn;

public class ImageRequest {
	int width;
	int height;
	int numberOfCharacters;
	
	public ImageRequest(int width, int height, int numberOfCharacters){
		this.width = width;
		this.height = height;
		this.numberOfCharacters = numberOfCharacters;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @return the numberOfCharacters
	 */
	public int getNumberOfCharacters() {
		return numberOfCharacters;
	}
}