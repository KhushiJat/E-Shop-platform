import { Pagination } from "@mui/material";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Paginations = ({ numberOfPage, totalProducts }) => {
    const [searchParams] = useSearchParams();
    const pathname = useLocation().pathname;
    const navigate = useNavigate();
    
    const paramValue = searchParams.get("page")
                ? Number(searchParams.get("page"))
                : 1;

    const onChangeHandler = (event, value) => {
        const params = new URLSearchParams(searchParams);
        params.set("page", value.toString());
        navigate(`${pathname}?${params.toString()}`);
    };

    return(
        <Pagination 
            count={numberOfPage || 1} 
            page={paramValue}
            defaultPage={1} 
            siblingCount={0} 
            boundaryCount={2} 
            shape="rounded" 
            onChange={onChangeHandler}
        />
    )
};

export default Paginations;