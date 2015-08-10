package de.tum.in.i22.uc.gui;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SceneGenerator {
	protected static Stage stage;
	protected static GuiController controller;

	protected Button btn_start;
	protected Button btn_stop;
	protected Button btn_deployPolicy;

	protected Button btn_refreshPip;
	protected Button btn_refreshSettings;
	protected TextArea txta_pipState;

	protected TabPane tabpane_center;

	protected Thread autoRefreshThread;
	protected boolean autoRefresh = false;
	protected String interval = "1";

	protected Label lab_info;

	protected TableView<DeployedMechanism> tv;

	public Node generateTop() {
		HBox hb = new HBox();
		hb.setSpacing(5.0);
		hb.setPadding(new Insets(10, 10, 10, 10));
		this.btn_start = new Button("Start Usage Control");
		this.btn_start.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				new Thread() {
					@Override
					public void run() {
						controller.startUc();
					}
				}.start();
			}
		});
		hb.getChildren().add(btn_start);

		this.btn_stop = new Button("Stop Usage Control");
		this.btn_stop.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				new Thread(){@Override
				public void run(){controller.stopUc();}}.start();
			}

		});
		hb.getChildren().add(btn_stop);
		return hb;
	}

	public Node generateCenter() {
		this.tabpane_center = new TabPane();

		Tab pdpTab = new Tab();
		pdpTab.setClosable(false);
		pdpTab.setText("PDP");
		pdpTab.setId("PDPTAB");
		pdpTab.setContent(generatePdpTab());

		final Tab pipTab = new Tab();
		pipTab.setClosable(false);
		pipTab.setText("PIP");
		pipTab.setId("PIPTAB");
		pipTab.setContent(generatePipTab());
		pipTab.setOnSelectionChanged(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				if (pipTab.isSelected()) {
					System.out.println("ddd");
					controller.refreshPipState();

					lab_info.setText("PIP refreshed");
				}
			}
		});

//		final Tab pmpTab = new Tab();
//		pmpTab.setClosable(false);
//		pmpTab.setText("PMP");
//		pmpTab.setId("PMPTAB");
//		pmpTab.setContent(generatePmpTab());

		this.tabpane_center.getTabs().addAll(pdpTab, pipTab);//, pmpTab);
		return this.tabpane_center;
	}

	private Node generatePmpTab(){
		StackPane pane = new StackPane();
		Label lab = new Label("PMP policy template generation goes here");
		pane.getChildren().add(lab);
		return pane;
	}

	private Node generatePipTab() {
		AnchorPane anchorpane = new AnchorPane();

		HBox hbox = new HBox();
		hbox.setSpacing(5);

		Button btn_clearPip = new Button("Clear PIP");
		btn_clearPip.setMaxHeight(30);
		btn_clearPip.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				controller.sendCleanupEvent();
				lab_info.setText("PIP cleared");
			}
		});
		hbox.getChildren().add(btn_clearPip);

		this.btn_refreshPip = new Button("Manual refresh");
		this.btn_refreshPip.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				controller.refreshPipState();
			}
		});
		this.btn_refreshPip.setMinHeight(30);
		hbox.getChildren().add(this.btn_refreshPip);

		Image btnImg = new Image(getClass().getResourceAsStream(
				"/img/settings.png"));
		ImageView iv = new ImageView(btnImg);
		iv.setFitWidth(25);
		iv.setFitHeight(25);
		iv.setId("iv1");
		this.btn_refreshSettings = new Button("", iv);
		this.btn_refreshSettings.setMaxHeight(25);
		this.btn_refreshSettings
				.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent arg0) {
						// TODO Auto-generated method stub
						showRefreshConfigPanel(stage);
					}
				});
		this.btn_refreshSettings
				.setTooltip(new Tooltip("Refresh configuration"));
		hbox.getChildren().add(this.btn_refreshSettings);
		AnchorPane.setTopAnchor(hbox, 10.0);
		AnchorPane.setLeftAnchor(hbox, 10.0);

		this.txta_pipState = new TextArea();
		this.txta_pipState.setStyle("-fx-font-family: 'Courier New', MONOSPACE;");
		AnchorPane.setTopAnchor(this.txta_pipState, 45.0);
		AnchorPane.setLeftAnchor(this.txta_pipState, 10.0);
		AnchorPane.setBottomAnchor(this.txta_pipState, 10.0);
		AnchorPane.setRightAnchor(this.txta_pipState, 10.0);

		anchorpane.getChildren().addAll(hbox, this.txta_pipState);

		return anchorpane;
	}

	private void showRefreshConfigPanel(Window owner) {
		final Stage dialog = new Stage();
		dialog.initOwner(owner);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setTitle("Refresh configuration");
		dialog.setResizable(false);

		VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 10, 10, 10));

		final Label lab = new Label("Interval in seconds");
		final TextField tf = new TextField(interval);

		final CheckBox cb = new CheckBox("Auto Refresh");
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				// TODO Auto-generated method stub
				if (arg2 == true) {
					lab.setDisable(false);
					tf.setDisable(false);
				} else {
					lab.setDisable(true);
					tf.setDisable(true);
				}
			}
		});

		Button b = new Button("OK");
		b.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				autoRefresh = cb.isSelected();
				interval = tf.getText();
				if (autoRefresh) {
					if(autoRefreshThread != null)
						autoRefreshThread.interrupt();
					autoRefreshThread = new Thread() {
						@Override
						public void run() {
							while (autoRefresh && !isInterrupted()) {
								controller.refreshPipState();
								try {
									Thread.sleep(Integer.parseInt(interval) * 1000);
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					};
					autoRefreshThread.start();
				}
				dialog.close();
			}
		});
		vbox.getChildren().addAll(cb, lab, tf, b);

		if (!autoRefresh) {
			lab.setDisable(true);
			tf.setDisable(true);
			cb.setSelected(false);
		} else {
			cb.setSelected(true);
		}

		Scene dialogScene = new Scene(vbox, 150, 120);
		dialog.setScene(dialogScene);
		dialog.sizeToScene();
		dialog.show();
	}

	private Node generatePdpTab() {
		AnchorPane anchorPane = new AnchorPane();
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(5);
		this.btn_deployPolicy = new Button("Deploy Policy");
		this.btn_deployPolicy.setMinHeight(30);
		this.btn_deployPolicy.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub

				FileChooser fc = new FileChooser();
				fc.setTitle("Choose Policy");
				fc.setInitialDirectory(new File("/home/alex/Policies"));
				File f = fc.showOpenDialog(SceneGenerator.stage);
				if (f != null) {
					controller.deployPolicyURIPmp(f.getAbsolutePath());
					updateDeployedPolicies(controller.listMechanisms());
				}
			}
		});

		// Map<String, List<String>> deployedMech =
		// controller.getDeployedMechanisms();
		this.tv = new TableView<DeployedMechanism>();
		TableColumn<DeployedMechanism, String> policyName = new TableColumn<DeployedMechanism, String>(
				"Policy Name");
		policyName
				.setCellValueFactory(new PropertyValueFactory<DeployedMechanism, String>(
						"policy"));

		TableColumn<DeployedMechanism, String> mechanismName = new TableColumn<DeployedMechanism, String>(
				"Mechanism Name");
		mechanismName
				.setCellValueFactory(new PropertyValueFactory<DeployedMechanism, String>(
						"mechanism"));

		this.tv.getColumns().add(policyName);
		this.tv.getColumns().add(mechanismName);
		this.tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		this.tv.setContextMenu(this.createPolicyManagementContextMenu());

		vbox.getChildren().addAll(this.btn_deployPolicy,this.tv);

		return vbox;
	}

	private ContextMenu createPolicyManagementContextMenu() {
		ContextMenu cm = new ContextMenu();

		MenuItem mi1 = new MenuItem("Revoke policy");
		mi1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				DeployedMechanism dm = tv.getSelectionModel().getSelectedItem();
				if (dm != null) {
					String policy = tv.getSelectionModel().getSelectedItem()
							.getPolicy();
					String mechanism = tv.getSelectionModel().getSelectedItem()
							.getMechanism();
					controller.revokeMechanism(policy, mechanism);
					updateDeployedPolicies(controller.listMechanisms());
				}
			}
		});
		cm.getItems().add(mi1);
		return cm;
	}

	protected void updateDeployedPolicies(
			Map<String, Set<String>> deployedPolicies) {
		Iterator<String> it = deployedPolicies.keySet().iterator();
		ObservableList<DeployedMechanism> data = FXCollections
				.observableArrayList();

		while (it.hasNext()) {
			String policy = it.next();
			Set<String> mechanisms = deployedPolicies.get(policy);
			Iterator<String> itMech = mechanisms.iterator();
			while (itMech.hasNext()) {
				String mechanism = itMech.next();
				DeployedMechanism dm = new DeployedMechanism(policy, mechanism);
				data.add(dm);
			}
		}
		this.tv.setItems(data);
	}

	protected void switchGuiCmp(boolean p) {
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				btn_deployPolicy.setDisable(!p);
				btn_refreshPip.setDisable(!p);
				btn_stop.setDisable(!p);
				tabpane_center.setDisable(!p);
				btn_start.setDisable(p);				
			}
		});
	}

	public Node generateBottom(){
		HBox hbox = new HBox();
		hbox.setSpacing(5);
		hbox.setPadding(new Insets(10,10,10,10));
		this.lab_info = new Label();
		hbox.getChildren().add(this.lab_info);

		return hbox;
	}
}
