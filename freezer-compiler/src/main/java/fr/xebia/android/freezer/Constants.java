package fr.xebia.android.freezer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class Constants {
    public static final String DAO_PACKAGE = "fr.xebia.android.freezer";

    public static final String DAO_CLASS_NAME = "Freezer";
    public static final String DATABASE_HELPER_CLASS_NAME = "DatabaseHelper";
    public static final String MIGRATOR = "Migrator";

    public static final String DAO_SUFFIX = "EntityManager";
    public static final String CURSOR_HELPER_SUFFIX = "CursorHelper";
    public static final String QUERY_BUILDER_SUFFIX = "QueryBuilder";
    public static final String ENUM_COLUMN_SUFFIX = "Columns";

    public static final TypeName daoClassName = ClassName.get(Constants.DAO_PACKAGE, DAO_CLASS_NAME);
    public static final TypeName dbHelperClassName = ClassName.get(Constants.DAO_PACKAGE, DATABASE_HELPER_CLASS_NAME);
    public static final TypeName queryBuilderClassName = ClassName.get(Constants.DAO_PACKAGE, QUERY_BUILDER_SUFFIX);
    public static final TypeName migrator = ClassName.get(Constants.DAO_PACKAGE+".migration", MIGRATOR);

    public static final TypeName applicationClassName = ClassName.get("android.app", "Application");
    public static final TypeName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");
    public static final TypeName sqliteOpenHelperClassName = ClassName.get("android.database.sqlite", "SQLiteOpenHelper");
    public static final TypeName contextClassName = ClassName.get("android.content", "Context");
    public static final TypeName cursorClassName = ClassName.get("android.database", "Cursor");
    public static final TypeName contentValuesClassName = ClassName.get("android.content", "ContentValues");
    public static final TypeName dateClassName = ClassName.get(Date.class);
    public static final TypeName simpleDateFormatClassName = ClassName.get(SimpleDateFormat.class);
    public static final TypeName stringBuilderClassName = ClassName.get(StringBuilder.class);

    public static final String ENUM_COLUMN_ELEMENT_NAME = "column_name";
    public static final String ENUM_COLUMN_IS_PRIMITIVE = "column_is_primitive";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "_field_name";
    public static final String QUERY_TABLE_VARIABLE = "t";
    public static final String QUERY_NAMED = "NAMED";

    public static final String PRIMITIVE_CURSOR_HELPER = "PrimitiveCursorHelper";
    public static final TypeName primitiveCursorHelper = ClassName.get(Constants.DAO_PACKAGE, PRIMITIVE_CURSOR_HELPER);
    public static final String PRIMITIVE_TABLE_INT = "MODEL_INT";
    public static final String PRIMITIVE_TABLE_LONG = "MODEL_INT";
    public static final String PRIMITIVE_TABLE_STRING = "MODEL_STRING";
    public static final String PRIMITIVE_TABLE_FLOAT = "MODEL_FLOAT";
    public static final String PRIMITIVE_TABLE_DOUBLE = "MODEL_FLOAT";
    public static final String PRIMITIVE_TABLE_BOOLEAN = "MODEL_BOOLEAN";

    public static final String SELECTOR_NUMBER = "NumberSelector";
    public static final String SELECTOR_NUMBER_LIST = "ListNumberSelector";
    public static final String SELECTOR_BOOLEAN = "BooleanSelector";
    public static final String SELECTOR_BOOLEAN_LIST = "ListBooleanSelector";
    public static final String SELECTOR_STRING = "StringSelector";
    public static final String SELECTOR_STRING_LIST = "ListStringSelector";
    public static final String SELECTOR_DATE = "DateSelector";

    public static final ClassName queryBuilder_NumberSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_NUMBER);
    public static final ClassName queryBuilder_ListNumberSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_NUMBER_LIST);
    public static final ClassName queryBuilder_BooleanSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_BOOLEAN);
    public static final ClassName queryBuilder_ListBooleanSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_BOOLEAN_LIST);
    public static final ClassName queryBuilder_StringSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_STRING);
    public static final ClassName queryBuilder_ListStringSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_STRING_LIST);
    public static final ClassName queryBuilder_DateSelectorClassName = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + QUERY_BUILDER_SUFFIX + "." + SELECTOR_DATE);

    public static final String QUERY_LOGGER = "QueryLogger";
    public static final String MODEL_ENTITY_PROXY = "Entity";
    public static final String MODEL_ENTITY_PROXY_INTERFACE = "DataBaseModel";
    public static final String MODEL_ENTITY_PROXY_GET_ID_METHOD = "getDatabaseModelId";
    public static final String MODEL_ENTITY_PROXY_SET_ID_METHOD = "setDatabaseModelId";

    public static final ClassName entityProxyClass = ClassName.bestGuess(Constants.DAO_PACKAGE + "." + MODEL_ENTITY_PROXY_INTERFACE);

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
