package com.github.florent37.orm;

import fr.xebia.android.freezer.annotations.Migration;
import fr.xebia.android.freezer.annotations.Version;
import fr.xebia.android.freezer.migration.ColumnType;
import fr.xebia.android.freezer.migration.FreezerMigrator;

/**
 * Created by florentchampigny on 28/01/2016.
 */
@Version(3)
public class DatabaseMigration {

    @Migration(2)
    public static void migrateTo2(FreezerMigrator freezerMigrator) {
        freezerMigrator.update("User")
                .transform("age").name("birth")
                //.transform("age").type(ColumnType.Int, ColumnType.Float)

                .name("MAN");
    }

    @Migration(3)
    public static void migrateTo3(FreezerMigrator freezerMigrator) {
        freezerMigrator.update("MAN")
                .removeField("name")
                .addField("lastName", ColumnType.String);
    }

    //@Migration(4)
    //public static void migrateTo4(FreezerMigrator freezerMigrator) {
    //    freezerMigrator
    //            .addTable(freezerMigrator.createObject("WOMAN")
    //                    .field("name", ColumnType.String)
    //                    .field("names", ColumnType.ListOfStrings))

    //            .update("MAN")
    //                .addField("lastName", ColumnType.ofTable("WOMAN"));
    //}

}
