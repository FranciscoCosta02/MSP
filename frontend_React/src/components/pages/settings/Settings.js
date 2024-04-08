import React, {useState} from 'react';
import vars from '../../../services/var/var';
import CodeEditor from '@uiw/react-textarea-code-editor';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';

const Settings = () => {
  const [code, setCode] = useState(JSON.stringify(vars));
  const [visibility, setVisibility] = useState(false);


  function changeVisibility(){
    if(visibility)
      setVisibility(false);
    else
      setVisibility(true);
  }


  return (
    <>
      <Box sx={{ minWidth: 120 }}>
      </Box>
      More:
      <IconButton aria-label="more" onClick={changeVisibility}>
        <MoreHorizIcon />
      </IconButton>
      {
        visibility &&
        <CodeEditor
          value={code}
          language="js"
          type='text/javascript'
          placeholder="Please enter JS code."
          disabled
          onChange={(evn) => setCode(evn.target.value)}
          padding={15}
          style={{
            fontSize: 11,
            backgroundColor: "#f5f5f5",
            fontFamily: 'ui-monospace,SFMono-Regular,SF Mono,Consolas,Liberation Mono,Menlo,monospace',
          }}
        />
      }
  </>
  );
};

export default Settings;
