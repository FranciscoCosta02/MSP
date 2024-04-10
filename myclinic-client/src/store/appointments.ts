import { createSlice, PayloadAction } from '@reduxjs/toolkit'

/*
import { Reservation, ReservationToAdd, OtherReservationsApi } from '../api'

export interface State {
    //appointments: Appointment[],
    filter: string,
    loading: boolean,
    uploading: boolean
}

const initialState: State = {
    //appointments: [],
    filter: '',
    loading: false,
    uploading: false
}

const api = new OtherReservationsApi();

const slice = createSlice({
    name: 'reservations',
    initialState,
    reducers: {
        addAppointment: (state, action: PayloadAction<ReservationToAdd>) => {
            state.uploading = false;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setReservations: (state, action: PayloadAction<Reservation[]>) => {
            state.reservations = action.payload;
            state.loading = false;
        },
        setUploadingReservation: (state, action: PayloadAction<boolean>) => {
            state.uploading = action.payload;
        },
        deleteReservations: (state) => {
            state.reservations = [];
        }
    }
});

export const {addReservation, deleteReservations, setLoading, setReservations, setUploadingReservation} = slice.actions;

export const loadReservations = (username: string) => (dispatch: any) => {
    dispatch(setLoading(true))
    api.getClientReservations(username)
        //.then(response => response.json())
        .then(reservations => dispatch(setReservations(reservations)))
}

export default slice.reducer
*/