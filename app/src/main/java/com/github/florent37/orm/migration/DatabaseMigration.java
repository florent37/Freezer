package com.github.florent37.orm.migration;

import fr.xebia.android.freezer.annotations.Migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class DatabaseMigration {

    @Migration(2)
    public static void update2(FreezerMigrator freezerMigrator) {
        freezerMigrator.update("User")
                .transform("age").name("birth")
                .transform("age").type(ColumnType.Int, ColumnType.Float)

                .name("MAN");
    }

    @Migration(3)
    public static void update3(FreezerMigrator freezerMigrator) {
        freezerMigrator.update("MAN")
                .removeField("name")
                .addField("lastName", ColumnType.String);
    }

    @Migration(4)
    public static void update4(FreezerMigrator freezerMigrator) {
        freezerMigrator
                .addTable(FreezerMigrator.createObject("WOMAN")
                        .field("name", ColumnType.String)
                        .field("names", ColumnType.ListOfStrings))

                .update("MAN")
                    .addField("lastName", ColumnType.ofTable("WOMAN"));
    }

}
