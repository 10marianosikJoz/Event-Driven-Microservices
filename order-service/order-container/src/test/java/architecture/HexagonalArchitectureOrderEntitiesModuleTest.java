package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureOrderEntitiesModuleTest {

    private static final String ORDER_SERVICE_PACKAGE = "com.product.ordering";

    @Test
    void entitiesModuleShouldNotDependOnContainerModule() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..container..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }

    @Test
    void entitiesModuleShouldNotDependOnAdaptersModule() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }
}
