import React, { Fragment } from "react";

import EstatisticaAcessos from "../../components/pages/estatisticas/estatisticaAcessos";


const EstatisticaUsers = () => (
  <Fragment>
    <h1>Statistics</h1>
    <p>It will only be possible to consult data from the last year of use.</p>
    <br/>
    <EstatisticaAcessos /> 
  </Fragment>
);

export default EstatisticaUsers;
