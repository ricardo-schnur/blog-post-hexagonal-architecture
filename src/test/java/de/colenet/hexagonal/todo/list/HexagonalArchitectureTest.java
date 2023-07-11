package de.colenet.hexagonal.todo.list;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = HexagonalArchitectureTest.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    static final String BASE_PACKAGE = "de.colenet.hexagonal.todo.list";

    @ArchTest
    static final ArchRule onionArchitectureIsRespected = Architectures
        .onionArchitecture()
        .domainModels(getPackageIdentifier("domain.model"))
        .domainServices(getPackageIdentifier("domain.service"))
        .applicationServices(getPackageIdentifier("application"))
        .adapter("cache", getAdapterIdentifier("cache"))
        .adapter("rest", getAdapterIdentifier("rest"))
        .withOptionalLayers(true) // TODO Remove this as soon as our layers are filled
        .ensureAllClassesAreContainedInArchitectureIgnoring(BASE_PACKAGE);

    private static String getAdapterIdentifier(String name) {
        return getPackageIdentifier("adapter." + name);
    }

    private static String getPackageIdentifier(String subpackage) {
        return BASE_PACKAGE + "." + subpackage + "..";
    }
}
