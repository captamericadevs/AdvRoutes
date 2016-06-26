package com.wordpress.captamericadevs.advroutes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wordpress.captamericadevs.advroutes.MapsActivity;
import com.wordpress.captamericadevs.advroutes.R;

public class ButtonPanel extends Fragment {

    //private ImageButton buttonRide;
    //private ImageButton buttonPlan;
    //private ImageButton buttonLoad;

    //private OnFragmentInteractionListener mListener;

    //private Context context;

    public ButtonPanel() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //context = container.getContext();
        View v = inflater.inflate(R.layout.fragment_button_panel, container, false);

//        buttonRide = (ImageButton) getView().findViewById(R.id.buttonRide);
//        buttonPlan = (ImageButton) getView().findViewById(R.id.buttonPlan);
//        buttonLoad = (ImageButton) getView().findViewById(R.id.buttonLoad);
//        buttonRide.setOnClickListener(this);
        // Inflate the layout for this fragment
        return v;
    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//      @Override
//      public void onClick(View v) {
//          if (v == buttonRide) {
//              ((MapsActivity)getActivity()).getDirections();
//          }
//      }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
