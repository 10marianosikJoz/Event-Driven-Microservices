package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureWarehouseEntitiesModuleTest {

    private static final String WAREHOUSE_SERVICE_PACKAGE = "com.product.ordering";

    @Test
    void entitiesModuleShouldNotDependOnContainerModule() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..container..")
                .check(new ClassFileImporter().importPackages(WAREHOUSE_SERVICE_PACKAGE));
    }

    @Test
    void entitiesModuleShouldNotDependOnAdaptersModule() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat(not(resideInAPackage("..adapters..")));
//                .check(new ClassFileImporter().importPackages(PAYMENT_SERVICE_PACKAGE));
    }
}
