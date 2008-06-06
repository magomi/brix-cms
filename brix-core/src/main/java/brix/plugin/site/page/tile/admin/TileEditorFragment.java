package brix.plugin.site.page.tile.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.fragment.TileContainer;
import brix.plugin.site.page.tile.Tile;

public abstract class TileEditorFragment extends Fragment<BrixNode>
{

    public TileEditorFragment(String id, String markupId, MarkupContainer markupProvider,
            final IModel<BrixNode> nodeModel, final String tileId)
    {
        super(id, markupId, markupProvider, nodeModel);

        final Form form = new Form("form");
        add(form);

        form.add(new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));

        Brix brix = nodeModel.getObject().getBrix();
        final String tileClassName = getTileContainerNode().tiles().getTileClassName(tileId);
        final Tile tile = Tile.Helper.getTileOfType(tileClassName, brix);

        final TileEditorPanel editor;

        form.add(editor = tile.newEditor("editor", nodeModel));

        editor.load(getTileContainerNode().tiles().getTile(tileId));

        form.add(new Button("submit")
        {
            @Override
            public void onSubmit()
            {
                BrixNode node = TileEditorFragment.this.getModelObject();
                BrixNode tile = getTileContainerNode().tiles().getTile(tileId);
                node.checkout();
                editor.save(tile);
                node.save();
                node.checkin();
            }
        });

        form.add(new Link("delete")
        {

            @Override
            public void onClick()
            {
                onDelete(tileId);
            }

            @Override
            protected void onComponentTag(ComponentTag tag)
            {
                super.onComponentTag(tag);
                tag.put("onclick",
                    "if (!confirm('Are you sure you want to remove this tile?')) return false; " +
                        tag.getAttributes().get("onclick"));
            }

        });
    }

    protected abstract void onDelete(String tileId);

    private TileContainer getTileContainerNode()
    {
        return (TileContainer)getModelObject();
    }

}
