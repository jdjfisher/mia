package io.github.mianalysis.mia.gui.regions.search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class SearchButton extends JButton implements ActionListener {
    private SearchPanel searchPanel;

    public SearchButton(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;

        setIcon(new ImageIcon(SearchPanel.class.getResource("/icons/search_black_12px.png"), ""));
        setPreferredSize(new Dimension(26, 26));
        setMinimumSize(new Dimension(26, 26));
        setMaximumSize(new Dimension(26, 26));
        addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        searchPanel.doSearch();

    }
}