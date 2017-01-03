package com.example.alizardo.thirdparty.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alizardo.thirdparty.R;
import com.example.alizardo.thirdparty.libs.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private boolean isPending;
    private JSONArray users;
    private int pos;
    private String token;
    private int partyId;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private int partyId;
        private View v;
        private JSONArray users;
        private String token;
        private ImageView url;
        private TextView name;
        private TextView email;
        private int id;
        private FloatingActionButton invite;

        public ViewHolder(String t, int partyId, View v, JSONArray u) {
            super(v);
            this.users = u;
            this.token = t;
            this.v = v;
            this.id = partyId;

            url = (ImageView) v.findViewById(R.id.users_layout_userDetailPic);
            name = (TextView) v.findViewById(R.id.users_layout_userDetailName);
            email = (TextView) v.findViewById(R.id.users_layout_userDetailEmail);
            invite = (FloatingActionButton) v.findViewById(R.id.accept_user);


            invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Inviting " + name.getText().toString() + ". Please wait.", Toast.LENGTH_SHORT).show();
                    HashMap<String, String> headers = new HashMap<>();
                    HashMap<String, String> payload = new HashMap<>();

                    headers.put("X-Auth-Token", token);
                    payload.put("Email", email.getText().toString());
                    payload.put("Id", String.valueOf(id));

                    invite.setVisibility(View.INVISIBLE);


                    new InviteUser().execute("/v1/event/invite", "POST", headers, payload);

                }
            });

        }

        public View getV() {
            return v;
        }


        class InviteUser extends AsyncTask<Object, Void, String> {

            protected void onPreExecute() {
            }

            protected String doInBackground(Object... params) {
                Utils util = new Utils();
                return util.request((String) params[0], (String) params[1], (HashMap) params[2], (HashMap) params[3]);
            }


            protected void onPostExecute(String response) {
                if (response == null || response.equals("")) {
                    Toast.makeText(getV().getContext(), "Something wrong happened.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getV().getContext(), "User invited to the party.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InviteAdapter(String token, int partyId, JSONArray users) {
        this.token = token;
        this.users = users;
        this.partyId = partyId;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return users.length();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
        ViewHolder vh = new ViewHolder(this.token, this.partyId, v, this.users);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject u = (JSONObject) users.get(position);
            // Change pic
            Context context = holder.url.getContext();
            Picasso.with(context).load(u.getString("pic")).into(holder.url);
            holder.url.setTag(u.getString("pic"));
            // Change name
            holder.name.setText(u.getString("name"));
            // Change email
            holder.email.setText(u.getString("email"));

            this.pos = position;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}