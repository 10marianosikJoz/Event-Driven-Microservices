package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureOrderDomainCoreModuleTest {

    private static final String ORDER_SERVICE_PACKAGE = "com.product.ordering";

    @Test
    void domainModuleShouldNotDependOnApplicationModule() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..application..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }

    @Test
    void domainModuleShouldNotDependOnAdaptersModule() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }

    @Test
    void domainModuleShouldNotDependOnContainerModule() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..container..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }

    @Test
    void domainShouldNotDependOnEntitiesModule() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..entities..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_PACKAGE));
    }
}
