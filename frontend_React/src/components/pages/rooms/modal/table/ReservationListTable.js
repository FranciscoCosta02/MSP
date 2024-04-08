import React, { useMemo,useState } from 'react';
import { MaterialReactTable } from 'material-react-table';
import {
    IconButton,
    Tooltip,
  } from '@mui/material';

  import BookmarkRemoveIcon from '@mui/icons-material/BookmarkRemove';
import { apiToken } from '../../../../../services/api/api';
import AlertComponent from '../../../../alerts/AlertComponent';
import vars from '../../../../../services/var/var';

export const ReservationListTable = (props) => {
  const {data,isRefetching,onDeleteReservation} = props;
  //Alert
  const [open, setOpen] = useState({open:false,type:"success",text:""});
  const handleClose = () => {
    setOpen({open:false,type:"success",text:""});
  };

  const columns = useMemo(
    //column definitions...
    () => [
      {
        accessorKey: 'roomName',
        header: 'roomName',
        enableHiding: false,
      },
      {
        accessorKey: 'roomDepartment',
        header: 'roomDepartment',
        enableHiding: false,
      },
      {
        accessorKey: 'time',
        header: 'time',
      },
      {
        accessorKey: 'username',
        header: 'username',
      },
      {
        accessorKey: 'date',
        header: 'date',
        enableHiding: false,
      },
      {
        accessorKey: 'weekDays',
        header: 'weekDays',
        enableHiding: false,
      },
    ],
    [],
    //end
  );

  async function delReservation(values,index){
    console.log(values);
    if (
        // eslint-disable-next-line no-restricted-globals
        !confirm(`Are you sure you want to cancel `+values.username+`'s booking? (`+values.time+`)`)
      ) {
        return;
      }

    try{
        const {data} = await apiToken.delete("/rooms/reserve",{data:values});
        console.log(data);
        setOpen({open:true,type:"success",text:vars.alerts.rooms.success_delete_booking});
        onDeleteReservation(index);
    }
    catch(error){
        console.log(error);
        setOpen({open:true,type:"error",text:vars.alerts.rooms.error_delete_booking});
    }
  }

  return (
    <>
    <MaterialReactTable
      columns={columns}
      data={data}
      initialState={{ columnVisibility: { roomName:false,roomDepartment: false,date:false,weekDays:false} }}
      enableColumnFilters={false}
      enablePagination={false}
      enableSorting={false}
      enableBottomToolbar={false}
      muiTableBodyRowProps={{ hover: false }}
      enableRowActions
      renderRowActions={({ row, table }) => (
        <>
            <Tooltip title="cancel reservation">
                <IconButton
                color="error"
                onClick={() => {
                    delReservation(row.original,row.index);
                }}
                >
                <BookmarkRemoveIcon />
                </IconButton>
            </Tooltip>
        </>
      )}
      state={{
        showProgressBars: isRefetching,
      }}
     
    />
    <AlertComponent
        openModal={open.open}
        type={open.type}
        text={open.text}
        onClose={handleClose}
    />
    </>
  );
};

export default ReservationListTable;
