const initialState = {
    cart: [],
    totalPrice: 0,
    cartId: null,
};

export const cartReducer = (state = initialState, action) => {
    let updatedCart;
    let newTotalPrice;

    switch (action.type) {
        case "ADD_CART": {
            const productToAdd = action.payload;
            const existingProduct = state.cart.find(
                (item) => item.productId === productToAdd.productId
            );

            if (existingProduct) {
                updatedCart = state.cart.map((item) => {
                    if (item.productId === productToAdd.productId) {
                        return { 
                            ...item, 
                            ...productToAdd,
                            quantity: productToAdd.quantity ?? item.quantity
                        };
                    }
                    return item;
                });
            } else {
                updatedCart = [...state.cart, productToAdd];
            }

            newTotalPrice = updatedCart.reduce(
                (acc, cur) => acc + Number(cur?.specialPrice || cur?.price || 0) * Number(cur?.quantity || 1),
                0
            );

            return {
                ...state,
                cart: updatedCart,
                totalPrice: newTotalPrice,
            };
        }

        case "REMOVE_CART": {
            updatedCart = state.cart.filter(
                (item) => item.productId !== action.payload.productId
            );

            newTotalPrice = updatedCart.reduce(
                (acc, cur) => acc + Number(cur?.specialPrice || cur?.price || 0) * Number(cur?.quantity || 1),
                0
            );

            return {
                ...state,
                cart: updatedCart,
                totalPrice: newTotalPrice,
            };
        }

        case "GET_USER_CART_PRODUCTS": {
            const cartItems = Array.isArray(action.payload) ? action.payload : [];
            const calculatedTotal = action.totalPrice ?? cartItems.reduce(
                (acc, cur) => acc + Number(cur?.specialPrice || cur?.price || 0) * Number(cur?.quantity || 1),
                0
            );

            return {
                ...state,
                cart: cartItems,
                totalPrice: calculatedTotal,
                cartId: action.cartId || state.cartId,
            };
        }

        case "CLEAR_CART":
            return { cart: [], totalPrice: 0, cartId: null };

        default:
            return state;
    }
};