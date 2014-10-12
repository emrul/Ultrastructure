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

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceEditor;

/**
 * @author hhildebrand
 *
 */
public class ExistentialRuleformView<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends JPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    @SuppressWarnings("rawtypes")
                    ExistentialRuleformView frame = new ExistentialRuleformView();
                    frame.setVisible(true);
                    JFrame enclosure = new JFrame();
                    enclosure.setBounds(0, 0, 600, 400);
                    enclosure.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    enclosure.getContentPane().add(frame);
                    enclosure.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        do {
            Thread.sleep(1000);
        } while (true);
    }

    private JTextField           name;
    private JTextField           description;
    protected RuleForm           ruleform;
    protected WorkspaceEditor    workspace;
    private JComboBox<Attribute> attributes;
    private AttributeValuePane   attributeValue;

    /**
     * Create the frame.
     */
    public ExistentialRuleformView() {
        // contentPane = new JPanel();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBounds(100, 100, 532, 172);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        add(lblName, gbc_lblName);

        name = new JTextField();
        GridBagConstraints gbc_name = new GridBagConstraints();
        gbc_name.insets = new Insets(0, 0, 5, 5);
        gbc_name.fill = GridBagConstraints.HORIZONTAL;
        gbc_name.gridx = 1;
        gbc_name.gridy = 0;
        add(name, gbc_name);
        name.setColumns(10);

        JLabel lblAttributes = new JLabel("Attributes");
        GridBagConstraints gbc_lblAttributes = new GridBagConstraints();
        gbc_lblAttributes.insets = new Insets(0, 0, 5, 5);
        gbc_lblAttributes.gridx = 2;
        gbc_lblAttributes.gridy = 0;
        add(lblAttributes, gbc_lblAttributes);

        attributes = new JComboBox<>();
        GridBagConstraints gbc_attributes = new GridBagConstraints();
        gbc_attributes.gridwidth = 2;
        gbc_attributes.insets = new Insets(0, 0, 5, 0);
        gbc_attributes.fill = GridBagConstraints.HORIZONTAL;
        gbc_attributes.gridx = 3;
        gbc_attributes.gridy = 0;
        add(attributes, gbc_attributes);

        JLabel lblDescription = new JLabel("Description");
        GridBagConstraints gbc_lblDescription = new GridBagConstraints();
        gbc_lblDescription.anchor = GridBagConstraints.EAST;
        gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
        gbc_lblDescription.gridx = 0;
        gbc_lblDescription.gridy = 1;
        add(lblDescription, gbc_lblDescription);

        description = new JTextField();
        GridBagConstraints gbc_description = new GridBagConstraints();
        gbc_description.insets = new Insets(0, 0, 5, 5);
        gbc_description.fill = GridBagConstraints.HORIZONTAL;
        gbc_description.gridx = 1;
        gbc_description.gridy = 1;
        add(description, gbc_description);
        description.setColumns(10);

        JLabel lblNotes = new JLabel("Notes");
        GridBagConstraints gbc_lblNotes = new GridBagConstraints();
        gbc_lblNotes.fill = GridBagConstraints.VERTICAL;
        gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
        gbc_lblNotes.gridx = 0;
        gbc_lblNotes.gridy = 2;
        add(lblNotes, gbc_lblNotes);

        JTextPane notes = new JTextPane();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.insets = new Insets(0, 0, 0, 5);
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.gridx = 1;
        gbc_notes.gridy = 2;
        add(notes, gbc_notes);

        attributeValue = new AttributeValuePane();
        GridBagConstraints gbc_attributeValue = new GridBagConstraints();
        gbc_attributeValue.gridheight = 2;
        gbc_attributeValue.gridwidth = 3;
        gbc_attributeValue.fill = GridBagConstraints.BOTH;
        gbc_attributeValue.gridx = 2;
        gbc_attributeValue.gridy = 1;
        add(attributeValue, gbc_attributeValue);

    }
}
