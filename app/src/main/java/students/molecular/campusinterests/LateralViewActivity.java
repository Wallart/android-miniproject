package students.molecular.campusinterests;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LateralViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView  listView;
    private DrawerLayout lateralView;
    private String[] lateralViewItems = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lateral_view);
        listView = (ListView)findViewById(R.id.list_view_drawer);
        lateralViewItems[0] = "Mes Photos";
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lateralViewItems));
        listView.setOnItemClickListener(this);
        lateralView = (DrawerLayout)findViewById(R.id.drawer_layout);
    }

    /**
     * Called when an item in the lateral view (Navigational drawer) is clicked
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lateralView.closeDrawer(Gravity.LEFT);
                if(position == 0) {
                    //Mes Photos selected => first and the only item of the list
                }
    }
}
