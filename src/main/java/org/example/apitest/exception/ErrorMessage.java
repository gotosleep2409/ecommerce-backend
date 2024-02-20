package org.example.apitest.exception;

public enum ErrorMessage {
	BAD_REQUEST(400, "bad_request"),

	INVALID_PARAM(40001, "invalid_input_params"),

	INVALID_DATE_FORMAT(40002, "invalid_date_format"),

	INVALID_UNIT(40002, "numberOfUnit must be Large than 0"),

	CONTRAINS_EXCEPTION(40003, "duplicate key value violates unique constraint"),

	CANNOT_LOGIN(40004, "can not login, please make sure params are correct!"),

	EMAIL_CANNOT_BE_NULL(40004, "email cannot be null!"),
	
	USER_EXISTED(40005, "this user existed, can not register"),

	EXERCISE_EXISTED(400025, "this exercise existed"),

	IDENTIFIER_EXISTED(400025, "identifier already exists in db"),

	REGISTER_ERROR(40006, "there is an error, can not register"),

	INVALID_ID_SERVING_SIZE(40006, "number_of_unit of servings in food must be greater than 0"),

	FORBIDDEN_API(40301, "cannot_access_api"),

	NOT_FOUND(404, "resource_not_found"),
	
	USER_MEAL_NOT_FOUND(40401, "meal not found"),

	EXERCISE_ALREADY_EXISTED(40401, "exercise already exists"),

	INVALID_ID_MEAL_PLAN(40402, "invalid id meal plan"),

	INVALID_ID_EXERCISE(140402, "invalid id exercise"),

	INVALID_ID_RECIPE(140402, "invalid id recipe"),

	INVALID_MEAL_TYPE(141402, "invalid meal type"),

	EXISTED_MEAL_PLAN_RECIPE(142402, "recipe existed in db"),

	INVALID_MEAL_FOOD_ID(142408, "mealFoodId not exist in db"),

	IS_NOT_PREMIUM(40412, "your account is not a premium account"),

	PRODUCT_ID_NOT_FOUND(40413, "product id not found"),

	ERROR_VALIDATION_FOR_SUBSCRIPTION_TRANSACTION(50413, "error validation for subscription transaction"),

	USER_SUBSCRIPTION_EXISTED(40413, "you are already bought this subscription, can't buy a subscription a new one"),

	MEAL_PLAN_IS_ACTIVATED(40403, "meal plan has been activated"),

	UNSUPPORTED_MEDIA_TYPE(415000, "Unsupported_Media_Type"),

	INCORRECT_OLD_PASSWORD(415001, "Old password is incorrect"),

	INVALIDATE_NEW_PASSWORD(415002, "The new password cannot be the same as the old password"),

	MEAL_PLAN_DOES_NOT_EXIST(4150022, "The meal plan does not exist!"),

	RECIPE_SUBJECT_DOES_NOT_EXIST(4150022, "The recipe subject does not exist!"),

	RECIPE_ID_DOES_NOT_EXIST(4150222, "Some recipeId of typical dishes mealPlan does not exist in db!"),

	CAN_NOT_DELETE_MEAL_PLAN(4150222, "Can't delete because there are users who are following this mealPlan!"),

	;

	/** The error code. */
	private int errorCode;

	/** The message. */
	private String message;

	/**
	 * Instantiates a new error message.
	 * 
	 * @param pCode    the code
	 * @param pMessage the message
	 */
	ErrorMessage(int pCode, String pMessage) {
		errorCode = pCode;
		message = pMessage;
	}

	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public int getCode() {
		return errorCode;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
