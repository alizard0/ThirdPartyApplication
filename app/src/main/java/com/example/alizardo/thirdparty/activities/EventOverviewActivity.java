package com.example.alizardo.thirdparty.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alizardo.thirdparty.R;
import com.example.alizardo.thirdparty.libs.Utils;
import com.example.alizardo.thirdparty.pojo.Event;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


public class EventOverviewActivity extends AppCompatActivity {

    private TextView description;
    private TextView startDate;
    private TextView endDate;
    private ImageView url;
    private TextView maxGuests;
    private TextView slotsLeft;
    private ImageView userPic;
    private TextView userName;
    private TextView userEmail;
    private String token;
    private int id;
    private LinearLayout hostButtons;
    private JSONArray usersAccepted, usersInvited, usersRejected, usersPending;
    private CoordinatorLayout askButton;
    private LinearLayout invitedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        this.token = b.getString("token");
        this.id = b.getInt("id");


        FloatingActionButton askToJoin = (FloatingActionButton) findViewById(R.id.fab);
        askToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You asked to join the party!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                FloatingActionButton btn = (FloatingActionButton) view.findViewById(R.id.fab);
                btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accept)));
                btn.setImageResource(R.drawable.check_circle);

                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                new AskParty().execute("/v1/event/ask?id=" + id, headers, payload);

            }
        });


        FloatingActionButton inviteUsers = (FloatingActionButton) findViewById(R.id.fabInvite);
        inviteUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                //done: ASYNC TASK to activity with all users to invite
                new InviteParty().execute("/v1/event/available?id=" + id, headers, payload);



            }
        });


        FloatingActionButton editParty = (FloatingActionButton) findViewById(R.id.fabEdit);
        editParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not implemented yet.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        FloatingActionButton requests = (FloatingActionButton) findViewById(R.id.fabAccepted);
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                new GetLists().execute("/v1/event/list?id=" + id, headers, payload);

            }
        });


        FloatingActionButton deleteParty = (FloatingActionButton) findViewById(R.id.fabDelete);
        deleteParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                payload.put("Id", String.valueOf(id));
                new DeleteParty().execute("/v1/event", "DELETE", headers, payload);
                onBackPressed();
            }
        });

        FloatingActionButton acceptInvitedParty = (FloatingActionButton) findViewById(R.id.fabAcceptInvitedParty);
        acceptInvitedParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                payload.put("Id", String.valueOf(id));
                new AcceptInviteParty().execute("/v1/event/accept?id=" + id, headers);
                onBackPressed();
            }
        });


        FloatingActionButton rejectInvitedParty = (FloatingActionButton) findViewById(R.id.fabRejectInvitedParty);
        rejectInvitedParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> payload = new HashMap<>();

                headers.put("X-Auth-Token", token);
                payload.put("Id", String.valueOf(id));
                new RejectInviteParty().execute("/v1/event/reject?id=" + id, headers);
                onBackPressed();
            }
        });

        View v = (View) findViewById(R.id.content_event_overview);

        this.description = (TextView) v.findViewById(R.id.description);
        this.startDate = (TextView) v.findViewById(R.id.startDate);
        this.endDate = (TextView) v.findViewById(R.id.endDate);
        this.maxGuests = (TextView) v.findViewById(R.id.maxGuests);
        this.slotsLeft = (TextView) v.findViewById(R.id.slotsLeft);
        this.userName = (TextView) v.findViewById(R.id.userDetailName);
        this.userEmail = (TextView) v.findViewById(R.id.userDetailEmail);
        this.hostButtons = (LinearLayout) v.findViewById(R.id.host_buttons);
        this.invitedButtons = (LinearLayout) v.findViewById(R.id.invited_buttons);
        this.askButton = (CoordinatorLayout) v.findViewById(R.id.fab);

        this.userPic = (ImageView) v.findViewById(R.id.userDetailPic);

        this.url = (ImageView) v.findViewById(R.id.pic);

        this.description.setText(b.getString("description"));
        this.startDate.setText(b.getString("startDate"));
        this.endDate.setText(b.getString("endDate"));
        this.maxGuests.setText(b.getString("maxGuests"));
        this.slotsLeft.setText(b.getString("slotsLeft"));
        this.userName.setText(b.getString("host_name"));
        this.userEmail.setText(b.getString("host_email"));

        if (((Event) b.getSerializable("evt")).isHost()) {
            Toast.makeText(this, "You are host of this event.", Toast.LENGTH_SHORT).show();
            askToJoin.setVisibility(View.GONE);
            this.invitedButtons.setVisibility(View.GONE);
        }else if(((Event) b.getSerializable("evt")).isRejected()){
            Toast.makeText(this, "You were rejected of this event.", Toast.LENGTH_SHORT).show();
            this.hostButtons.setVisibility(View.GONE);
            askToJoin.setVisibility(View.GONE);
            this.invitedButtons.setVisibility(View.GONE);
        } else if(((Event) b.getSerializable("evt")).isAccepted()){
            Toast.makeText(this, "You were accepted to join this event.", Toast.LENGTH_SHORT).show();
            askToJoin.setVisibility(View.GONE);
            this.invitedButtons.setVisibility(View.GONE);
            this.hostButtons.setVisibility(View.GONE);
        } else if(((Event) b.getSerializable("evt")).isInvited()){
            Toast.makeText(this, "You were invited to join this event.", Toast.LENGTH_SHORT).show();
            askToJoin.setVisibility(View.GONE);
            this.hostButtons.setVisibility(View.GONE);
        } else if(((Event) b.getSerializable("evt")).isPending()){
            Toast.makeText(this, "Your request is pending.", Toast.LENGTH_SHORT).show();
            this.hostButtons.setVisibility(View.GONE);
            askToJoin.setVisibility(View.GONE);
            this.invitedButtons.setVisibility(View.GONE);
        } else {
            this.hostButtons.setVisibility(View.GONE);
            this.invitedButtons.setVisibility(View.GONE);
        }


        Context context = getApplicationContext();
        Picasso.with(context).load(b.getString("host_URL")).into(this.userPic);
        Picasso.with(context).load(b.getString("url")).into(this.url);

        setTitle(b.getString("title"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


    class AskParty extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {
            Log.i("STATUS", "Ask to join the party.");
        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.requestGET((String) params[0], (HashMap) params[1]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response);
                Snackbar.make(findViewById(android.R.id.content), map.get("Result").toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class DeleteParty extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {
            Log.i("STATUS", "Delete party.");
        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.request((String) params[0], (String) params[1], (HashMap) params[2], (HashMap) params[3]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response);
                Snackbar.make(findViewById(android.R.id.content), map.get("Result").toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class GetLists extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {
            Log.i("STATUS", "Getting lists.");
        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.requestGET((String) params[0], (HashMap) params[1]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response).getJSONObject("Result");
                usersAccepted = map.getJSONArray("usersAccepted");
                usersInvited = map.getJSONArray("usersInvited");
                usersPending = map.getJSONArray("usersPending");
                usersRejected = map.getJSONArray("usersRejected");
                Intent i = new Intent(EventOverviewActivity.this, EventRequestsActivity.class);
                i.putExtra("usersAccepted", usersAccepted.toString());
                i.putExtra("usersInvited", usersInvited.toString());
                i.putExtra("usersPending", usersPending.toString());
                i.putExtra("usersRejected", usersRejected.toString());
                i.putExtra("Id", String.valueOf(id));
                i.putExtra("token", token);
                startActivity(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class AcceptInviteParty extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.requestGET((String) params[0], (HashMap) params[1]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response);
                Snackbar.make(findViewById(android.R.id.content), map.get("Result").toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class RejectInviteParty extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.requestGET((String) params[0], (HashMap) params[1]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response);
                Snackbar.make(findViewById(android.R.id.content), map.get("Result").toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class InviteParty extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {
            Log.i("STATUS", "Getting friends list.");
        }

        protected String doInBackground(Object... params) {
            Utils util = new Utils();
            return util.requestGET((String) params[0], (HashMap) params[1]);
        }

        protected void onPostExecute(String response) {
            if (response == null || Objects.equals(response, "")) {
                return;
            }

            JSONObject map = null;

            try {
                map = new JSONObject(response);
                Intent intent = new Intent(EventOverviewActivity.this, InviteFriendsActivity.class);
                intent.putExtra("token",token);
                intent.putExtra("map",map.toString());
                intent.putExtra("id",id);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
