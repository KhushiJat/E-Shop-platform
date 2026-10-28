import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useSearchParams } from "react-router-dom";
import { dashboardProductsAction, fetchProducts } from "../store/actions";

export const useProductFilter = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();

    useEffect(() => {
        const apiParams = new URLSearchParams();

        // 1. Always get page number from URL or default to 1 (which maps to 0 for Spring Boot)
        const rawPage = searchParams.get("page");
        const currentPage = rawPage ? Number(rawPage) : 1;
        const pageNumber = currentPage > 0 ? currentPage - 1 : 0;

        apiParams.set("pageNumber", pageNumber.toString());
        apiParams.set("pageSize", "8");

        // 2. Add sorting, category, and keyword parameters if present
        const sortOrder = searchParams.get("sortby") || "asc";
        const categoryParams = searchParams.get("category");
        const keyword = searchParams.get("keyword");

        apiParams.set("sortBy", "price");
        apiParams.set("sortOrder", sortOrder);

        if (categoryParams) {
            apiParams.set("category", categoryParams);
        }

        if (keyword) {
            apiParams.set("keyword", keyword);
        }

        // 3. Dispatch fetchProducts with full explicit parameters on initial load
        dispatch(fetchProducts(apiParams.toString()));

    }, [dispatch, searchParams]);
};

export const useDashboardProductFilter = () => {
    const { user } = useSelector((state) => state.auth);
    const isAdmin = user && user?.roles?.includes("ROLE_ADMIN");

    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();

    useEffect(() => {
        const apiParams = new URLSearchParams();

        const rawPage = searchParams.get("page");
        const currentPage = rawPage ? Number(rawPage) : 1;
        const pageNumber = currentPage > 0 ? currentPage - 1 : 0;

        apiParams.set("pageNumber", pageNumber.toString());
        apiParams.set("pageSize", "8");

        const sortOrder = searchParams.get("sortby") || "asc";
        const categoryParams = searchParams.get("category");
        const keyword = searchParams.get("keyword");

        apiParams.set("sortBy", "price");
        apiParams.set("sortOrder", sortOrder);

        if (categoryParams) {
            apiParams.set("category", categoryParams);
        }

        if (keyword) {
            apiParams.set("keyword", keyword);
        }

        dispatch(dashboardProductsAction(apiParams.toString(), isAdmin));

    }, [dispatch, searchParams, isAdmin]);
};

export default useProductFilter;
