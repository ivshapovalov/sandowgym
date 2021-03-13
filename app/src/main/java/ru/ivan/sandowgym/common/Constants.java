package ru.ivan.sandowgym.common;

import ru.ivan.sandowgym.database.entities.User;

public class Constants {
    public static final String BACKUP_FILE_DATE_PATTERN = "yyyyMMdd-HHmmss";
    public static final int MAX_NOTIFICATION_CHARSEQUENCE_LENGTH = 5 * 1024;
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final boolean IS_DEBUG = true;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS = "rows_on_page_in_lists";
    public static final String APP_PREFERENCES_BACKUP_LOCAL_FOLDER = "backup_local_folder";
    public static final String APP_PREFERENCES_BACKUP_FTP_HOST = "backup_ftp_host";
    public static final String APP_PREFERENCES_BACKUP_FTP_LOGIN = "backup_ftp_login";
    public static final String APP_PREFERENCES_BACKUP_FTP_PASSWORD = "backup_ftp_password";
    public static final String APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN = "backup_dropbox_access_token";
    public static final String APP_PREFERENCES_BACKUP_SCHEDULE_ENABLED = "backup_schedule_enabled";
    public static final String APP_PREFERENCES_BACKUP_SCHEDULE_TIME_HOUR = "backup_schedule_time_hour";
    public static final String APP_PREFERENCES_BACKUP_SCHEDULE_TIME_MINUTES = "backup_schedule_time_minutes";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON = "training_show_amount_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON = "training_show_amount_last_day_button";
    public static final String APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE = "training_plus_minus_button_value";
    public static final String APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT = "training_use_calendar_for_weight";

    public static int mOptionRowsOnPageInLists;
    public static String mOptionBackupLocalFolder;
    public static String mOptionBackupFtpHost;
    public static String mOptionBackupFtpLogin;
    public static String mOptionBackupFtpPassword;
    public static String mOptionBackupDropboxAccessToken;
    public static boolean mOptionBackupScheduleEnabled;
    public static int mOptionBackupScheduleDateTimeHour;
    public static int mOptionBackupScheduleDateTimeMinutes;

    public static User dbCurrentUser;
    public static volatile boolean processingInProgress;
}
