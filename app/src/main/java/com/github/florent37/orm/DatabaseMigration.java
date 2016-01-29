package com.github.florent37.orm;

import fr.xebia.android.freezer.annotations.Migration;
import fr.xebia.android.freezer.migration.Migrator;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class DatabaseMigration {

    @Migration(2)
    public static void migrateTo2(Migrator migrator) {
        migrator.update("User")
                .renameTo("Man");

        //migrator.update("User")
        //        .removeField("age");
                //.addField("birth", ColumnType.Primitive.Int)
                //.name("User");
    }

    //@Migration(3)
    //public static void migrateTo3(Migrator migrator) {
    //    migrator.update("User")
    //            .addField("birth", ColumnType.Primitive.Int);
    //}

    //@Migration(4)
    //public static void migrateTo4(Migrator migrator) {
    //    migrator.update("User")
    //            .name("User");
    //}


    //@Migration(3)
    //public static void migrateTo3(Migrator migrator) {
    //    migrator.update("User")
    //            .removeField("name")
    //            .addField("lastName", ColumnType.Primitive.String);
    //}

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
