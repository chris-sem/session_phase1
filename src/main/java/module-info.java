module isty.iatic5.session_phase1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens isty.iatic5.session_phase1 to javafx.fxml;
    exports isty.iatic5.session_phase1;
}