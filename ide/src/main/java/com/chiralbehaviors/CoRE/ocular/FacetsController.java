/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.ocular;

import java.io.IOException;

import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @author hhildebrand
 *
 */
public class FacetsController {
    private static String QUERY = "{ facets { id name } }";
    private static String EXISTENTIALS_QUERY;

    static {
        try {
            EXISTENTIALS_QUERY = Utils.getDocument(FacetController.class.getResourceAsStream("existentials.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected GraphQlApi           api;

    @FXML
    protected AnchorPane           facetAnchor;

    protected FacetController      facetController;

    @FXML
    protected ListView<ObjectNode> facets;

    public GraphQlApi getApi() {
        return api;
    }

    @FXML
    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Ocular.class.getResource("view/FacetView.fxml"));
        GridPane facetsView = (GridPane) loader.load();
        AnchorPane.setTopAnchor(facetsView, 10.0);
        AnchorPane.setBottomAnchor(facetsView, 10.0);
        AnchorPane.setLeftAnchor(facetsView, 10.0);
        AnchorPane.setRightAnchor(facetsView, 10.0);
        facetAnchor.getChildren()
                   .add(facetsView);
        facetController = loader.getController();
        facets.setCellFactory(cellData -> new ListCell<ObjectNode>() {
            @Override
            protected void updateItem(ObjectNode item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && (item != null)) {
                    setText(item.get("name")
                                .asText());
                } else {
                    setText(null);
                }
            }
        });
        facets.getSelectionModel()
              .selectedItemProperty()
              .addListener(new ChangeListener<ObjectNode>() {
                  @Override
                  public void changed(ObservableValue<? extends ObjectNode> ov,
                                      ObjectNode old_val, ObjectNode new_val) {
                      if (new_val == null) {
                          facetController.setFacet(null);
                      } else {
                          facetController.setFacet(new_val.get("id")
                                                          .asText());
                      }
                  }
              });
    }

    public void setApi(GraphQlApi api) {
        this.api = api;
        ObjectNode existentials;
        try {
            existentials = api.query(EXISTENTIALS_QUERY, null);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        facetController.setApi(api, existentials);
    }

    public void update() {
        ObjectNode f;
        try {
            f = api.query(QUERY, null);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        ObservableList<ObjectNode> facetList = FXCollections.observableArrayList();
        f.withArray("facets")
         .forEach(o -> facetList.add((ObjectNode) o));
        facets.setItems(facetList);
    }
}
