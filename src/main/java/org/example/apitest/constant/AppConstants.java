package org.example.apitest.constant;

public class AppConstants {

	public static final String HEADER_STRING = "Authorization";

	public static final String TOKEN_PREFIX = "Bearer ";

	public static final String TOKEN_TYPE = "bearer";

	public static final String DEFAULT_SECRET = "8f821a74-367b-4741-95b6-fdfad9b44705";

	public static final long EXPIRATION_TIME = 86400000L; //1day

	public static int DEFAULT_PAGE = 1;

	public static int DEFAULT_PAGE_SIZE = 10;

	public static int MAX_PAGE_SIZE = 200;

	public static long DAY_IN_MS = 1000 * 60 * 60 * 24;

	public static final String DEFAULT_TZ = "GMT+7";
	public static final String ACCESS_API_PATH_KEY = "product_api";
	
	public static int MAX_MSG_COUNT_PER_DAY = 10;
	public static int MAX_IMG_COUNT_PER_DAY = 5;
	public static int MAX_AUDIO_COUNT_PER_DAY = 5;
	
	public static int MAX_FAKE_IMG_RESPONSE = 5;
	
	public static int MAX_MJ_ERROR_COUNT = 3;
	
	public static int ENERGY_PER_GEN = 10;
	
	public static int TIMESTAMP_7DAY = 7 * 24 * 60 * 60; // 7 days
	
	public static int TIMESTAMP_1DAY = 1 * 24 * 60 * 60; // 1 days
	
	public static final String AITO_PACKAGE_NAME = "com.smartai.smartimage";
	public static final String SAI_PACKAGE_NAME = "com.chatcpt.aichat";
	public static final String WEAVER_PACKAGE_NAME = "com.artweaver.ai";
	
	public static final String PLATFORM_MJ = "MJ";
	public static final String PLATFORM_BING = "BING";
	public static final String PLATFORM_OPENAI = "OPENAI";
	
	public static final String IMG_GEN_TYPE_CARTOON = "CARTOON";
	public static final String IMG_GEN_TYPE_NORMAL = "NORMAL";

	public static final int DAY_ONE = 1;

	public static final int DAY_TWO = 2;

	public static final int DAY_THREE = 3;

	public static final int DAY_FOR = 4;

	public static final int DAY_FIRE = 5;

	public static final int DAY_SIX = 6;

	public static final int DAY_SEVEN = 7;
	
	public static final String MJ_PERCENTAGE_KEY = "mj_percentage_after_bing";
	public static final String FREE_MJ_OPTION_KEY = "free_mj_options";
	public static final String IS_ONLY_AMZ_VPS_KEY = "is_only_amz_vps";
	public static final String PROMPT_OPTIMIZATION_FORMAT = "prompt_optimization_format";
}
