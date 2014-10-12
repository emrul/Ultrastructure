/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class ParentSeqAuthView extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JLabel                lblSequence;
    private JSpinner              sequenceNumber;
    private JCheckBox             chckbxSetIfActive;
    private JLabel                lblParent;
    private JLabel                lblNextStatus;
    private JCheckBox             chckbxReplaceProduct;
    private JComboBox<Product>    parent;
    private JComboBox<StatusCode> parentStatusToSet;

    /**
     * Create the panel.
     */
    public ParentSeqAuthView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.gridwidth = 2;
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 0;
        add(parent, gbc_parent);

        lblNextStatus = new JLabel("Status To Set");
        GridBagConstraints gbc_lblNextStatus = new GridBagConstraints();
        gbc_lblNextStatus.anchor = GridBagConstraints.EAST;
        gbc_lblNextStatus.insets = new Insets(0, 0, 5, 5);
        gbc_lblNextStatus.gridx = 0;
        gbc_lblNextStatus.gridy = 1;
        add(lblNextStatus, gbc_lblNextStatus);

        parentStatusToSet = new JComboBox<>();
        GridBagConstraints gbc_parentStatusToSet = new GridBagConstraints();
        gbc_parentStatusToSet.gridwidth = 2;
        gbc_parentStatusToSet.insets = new Insets(0, 0, 5, 0);
        gbc_parentStatusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_parentStatusToSet.gridx = 1;
        gbc_parentStatusToSet.gridy = 1;
        add(parentStatusToSet, gbc_parentStatusToSet);

        lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.anchor = GridBagConstraints.EAST;
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 0;
        gbc_lblSequence.gridy = 2;
        add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.anchor = GridBagConstraints.WEST;
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 5);
        gbc_sequenceNumber.gridx = 1;
        gbc_sequenceNumber.gridy = 2;
        add(sequenceNumber, gbc_sequenceNumber);

        chckbxSetIfActive = new JCheckBox("Set If Active Siblings");
        GridBagConstraints gbc_chckbxSetIfActive = new GridBagConstraints();
        gbc_chckbxSetIfActive.anchor = GridBagConstraints.WEST;
        gbc_chckbxSetIfActive.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxSetIfActive.gridx = 2;
        gbc_chckbxSetIfActive.gridy = 2;
        add(chckbxSetIfActive, gbc_chckbxSetIfActive);

        chckbxReplaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_chckbxReplaceProduct = new GridBagConstraints();
        gbc_chckbxReplaceProduct.anchor = GridBagConstraints.WEST;
        gbc_chckbxReplaceProduct.gridx = 2;
        gbc_chckbxReplaceProduct.gridy = 3;
        add(chckbxReplaceProduct, gbc_chckbxReplaceProduct);

    }

}
