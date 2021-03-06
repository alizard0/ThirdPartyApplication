package com.example.alizardo.thirdparty.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alizardo.thirdparty.R;
import com.example.alizardo.thirdparty.adapters.EventAdapter;
import com.example.alizardo.thirdparty.libs.Utils;
import com.example.alizardo.thirdparty.pojo.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsTabFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "page_number";

    private OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private JSONObject data;
    private RecyclerView.LayoutManager mLayoutManager;
    TabLayout tabLayout;
    private int page;
    private String token;


    public EventsTabFragment() {
        // Required empty public constructor
    }

    public static EventsTabFragment newInstance(String token, JSONObject data, int page) {
        EventsTabFragment fragment = new EventsTabFragment();
        Bundle args = new Bundle();
        args.putString("events", data.toString());
        args.putInt(ARG_PAGE_NUMBER, page);
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.token = getArguments().getString("token");
                this.data = new JSONObject(getArguments().getString("events"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.page = getArguments().getInt(ARG_PAGE_NUMBER, -1);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events_tab, container, false);

        this.mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        Utils u = new Utils();

        JSONArray jsonArray = null;
        List<Event> myDataset = new ArrayList<>();
        try {
            jsonArray = this.data.getJSONArray("Result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                Event e = new Event(explrObject.get("title").toString(), explrObject.get("local").toString(),
                        explrObject.get("description").toString(), explrObject.get("startDate").toString(), explrObject.get("endDate").toString(),
                        explrObject.get("maxGuests").toString(), explrObject.get("URL").toString(), explrObject.get("slotsLeft").toString(),
                        explrObject.get("host_name").toString(), explrObject.get("host_email").toString(),
                        explrObject.get("host_URL").toString(), explrObject.get("id").toString(),
                        Boolean.parseBoolean((String) explrObject.get("isHosting")), Boolean.parseBoolean((String) explrObject.get("isAccepted")),
                        Boolean.parseBoolean((String) explrObject.get("isInvited")), Boolean.parseBoolean((String) explrObject.get("isPending")),
                        Boolean.parseBoolean((String) explrObject.get("isRejected"))
                );
                myDataset.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // specify an adapter
        this.mAdapter = new EventAdapter(token, myDataset);
        this.mAdapter.notifyDataSetChanged();
        // use a linear layout manager
        this.mLayoutManager = new LinearLayoutManager(getActivity());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.setAdapter(this.mAdapter);


        return v;

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
