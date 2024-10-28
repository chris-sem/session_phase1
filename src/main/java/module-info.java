module isty.iatic5.session_phase1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens isty.iatic5.session_phase1 to javafx.fxml;
    exports isty.iatic5.session_phase1;
    exports isty.iatic5.session_phase1.Controller;
    opens isty.iatic5.session_phase1.Controller to javafx.fxml;
}