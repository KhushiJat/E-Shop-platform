import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { useDispatch } from "react-redux";

import { getAllCategoriesDashboard } from "../store/actions"; // Use the dashboard version

const useCategoryFilter = () => {
  const [searchParams] = useSearchParams(); 
  const dispatch = useDispatch(); 

  useEffect(() => {
    const params = new URLSearchParams(); 

    const currentPage = searchParams.get("page")
      ? Number(searchParams.get("page"))
      : 1;
    params.set("pageNumber", currentPage - 1); 

    const queryString = params.toString();

    // Call the dashboard action which dispatches CATEGORY_SUCCESS to clear the loader
    dispatch(getAllCategoriesDashboard(queryString));
  }, [dispatch, searchParams]);
};

export default useCategoryFilter;