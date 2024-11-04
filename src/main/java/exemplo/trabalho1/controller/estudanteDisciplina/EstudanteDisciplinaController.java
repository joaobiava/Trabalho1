package exemplo.trabalho1.controller.estudanteDisciplina;

import exemplo.trabalho1.dao.EstudanteDAO;
import exemplo.trabalho1.dao.DisciplinaDAO;
import exemplo.trabalho1.model.Disciplina;
import exemplo.trabalho1.model.Estudante;
import exemplo.trabalho1.model.EstudanteDisciplina;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudanteDisciplinaController {
    @FXML
    private Button btnConf;

    @FXML
    private TextField txtNomEst;

    @FXML
    private TextField idInput;

    @FXML
    private TextField nomeInput;

    @FXML
    private TextField estudanteId;

    @FXML
    private ComboBox<String> disciplinaComboBox; // ComboBox para selecionar disciplinas

    private DisciplinaDAO disciplinaDAO;
    private EstudanteDAO estudanteDAO;

    public EstudanteDisciplinaController() {
        this.estudanteDAO = new EstudanteDAO();
        this.disciplinaDAO = new DisciplinaDAO();
    }

    @FXML
    public void initialize() {
        try {
            // Chama o método listar() do DisciplinaDAO para obter todas as disciplinas
            List<Disciplina> disciplinas = disciplinaDAO.listar();

            // Extrai os nomes das disciplinas e adiciona ao ComboBox
            List<String> nomesDisciplinas = new ArrayList<>();
            for (Disciplina disciplina : disciplinas) {
                nomesDisciplinas.add(disciplina.getNome());
            }

            // Define os nomes das disciplinas no ComboBox
            disciplinaComboBox.setItems(FXCollections.observableArrayList(nomesDisciplinas));
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar disciplinas.", ButtonType.OK);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível carregar as disciplinas.");
            alert.show();
        }
    }


    @FXML
    private void btnAlterar(ActionEvent event) {
        Estudante estudante = new Estudante();
        estudante.setNome(nomeInput.getText());
        estudante.setEstudanteID(Integer.parseInt(idInput.getText()));

        try {
            estudanteDAO.update(estudante);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudante alterado com sucesso!", ButtonType.OK);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Informação");
            alert.show();
        } catch (SQLException e1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível alterar o estudante.", ButtonType.OK);
            alert.setTitle("Erro");
            alert.setHeaderText("Informação");
            alert.show();
            e1.printStackTrace();
        }
    }

    @FXML
    private void btnRemoverEstudante(ActionEvent event) {
        try {
            estudanteDAO.delete(estudanteDAO.consultar(Integer.parseInt(estudanteId.getText())));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudante removido com sucesso!", ButtonType.OK);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Informação");
            alert.show();
        } catch (SQLException e1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível remover o estudante.", ButtonType.OK);
            alert.setTitle("Erro");
            alert.setHeaderText("Informação");
            alert.show();
            e1.printStackTrace();
        }
    }

    @FXML
    private void btnAdicionarEstudante(ActionEvent event) {
        Estudante estudante = new Estudante();
        estudante.setNome(txtNomEst.getText());

        // Obtém a disciplina selecionada no ComboBox
        String disciplinaSelecionada = disciplinaComboBox.getValue();

        if (disciplinaSelecionada != null) {
            try {
                estudanteDAO.inserir(estudante);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudante cadastrado e associado à disciplina " + disciplinaSelecionada + " com sucesso!", ButtonType.OK);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Informação");
                alert.show();
            } catch (SQLException e1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar o estudante.", ButtonType.OK);
                alert.setTitle("Erro");
                alert.setHeaderText("Informação");
                alert.show();
                e1.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma disciplina antes de cadastrar o estudante.", ButtonType.OK);
            alert.setTitle("Aviso");
            alert.setHeaderText("Informação");
            alert.show();
        }
    }

    @FXML
    void btnVoltarOnAction(ActionEvent event) {
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();
    }

    @FXML
    private void btnAdicionarEstudanteADisciplina(ActionEvent event) {
        try {
            int estudanteId = Integer.parseInt(idInput.getText());
            String disciplinaNome = disciplinaComboBox.getSelectionModel().getSelectedItem();

            // Consulte o ID da disciplina com base no nome selecionado
            Disciplina disciplina = disciplinaDAO.consultarPorNome(disciplinaNome);
            if (disciplina != null) {
                disciplinaDAO.adicionarEstudanteADisciplina(estudanteId, disciplina.getDisciplina_id());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudante adicionado à disciplina com sucesso!", ButtonType.OK);
                alert.show();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao adicionar estudante à disciplina.", ButtonType.OK);
            alert.show();
        }
    }

    @FXML
    private void btnRemoverEstudanteDaDisciplina(ActionEvent event) {
        try {
            int estudanteId = Integer.parseInt(idInput.getText());
            String disciplinaNome = disciplinaComboBox.getSelectionModel().getSelectedItem();

            // Consulte o ID da disciplina com base no nome selecionado
            Disciplina disciplina = disciplinaDAO.consultarPorNome(disciplinaNome);
            if (disciplina != null) {
                // Tenta remover estudante da disciplina e verifica se a remoção foi bem-sucedida
                boolean sucesso = disciplinaDAO.removerEstudanteDaDisciplina(estudanteId, disciplina.getDisciplina_id());
                if (sucesso) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudante removido da disciplina com sucesso!", ButtonType.OK);
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Não foi possível remover: verifique se o estudante está matriculado nessa disciplina.", ButtonType.OK);
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Disciplina não encontrada.", ButtonType.OK);
                alert.show();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao remover estudante da disciplina.", ButtonType.OK);
            alert.show();
        }
    }



}