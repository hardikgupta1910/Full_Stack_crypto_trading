import api from "@/config/api";
import * as types from "./ActionTypes";
export const WITHDRAWAL_REQUEST = "WITHDRAWAL_REQUEST";
export const WITHDRAWAL_SUCCESS = "WITHDRAWAL_SUCCESS";
export const WITHDRAWAL_FAILURE = "WITHDRAWAL_FAILURE";



export const getUserWallet = (jwt) => async (dispatch) => {
  console.log("getUserWallet called with JWT:", jwt);
  dispatch({ type: types.GET_USER_WALLET_REQUEST });

  try {
    const response = await api.get(`/api/wallet`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
    console.log("Response from /api/wallet:", response.data);
    dispatch({
      type: types.GET_USER_WALLET_SUCCESS,
      payload: response.data,
    });

    return response.data; // ✅ RETURN wallet data
  } catch (error) {
    console.log("Error fetching wallet:", error);
    dispatch({
      type: types.GET_USER_WALLET_FAILURE,
      error: error.message,
    });
    throw error;
  }
};



export const getWalletTransactions =
  ({ jwt, walletId }) =>
  async (dispatch) => {
    dispatch({ type: types.GET_WALLET_TRANSACTION_REQUEST });

    try {
      const response = await api.post(
        "/api/transactions/wallet",
        { id: walletId },
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );

      dispatch({
        type: types.GET_WALLET_TRANSACTION_SUCCESS,
        payload: response.data,
      });
    } catch (error) {
      dispatch({
        type: types.GET_WALLET_TRANSACTION_FAILURE,
        error: error.message,
      });
    }
  };

export const depositeMoney =
  ({ jwt, orderId, paymentId, navigate }) =>
  async (dispatch) => {
    dispatch({ type: types.DEPOSIT_MONEY_REQUEST });

    try {
      const response = await api.get(`/api/wallet/deposit`, null, {
        params: {
          order_id: orderId,
          payment_id: paymentId,
        },
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      dispatch({
        type: types.DEPOSIT_MONEY_SUCCESS,
        payload: response.data,
      });
      navigate("/wallet");
      console.log(response.data);
    } catch (error) {
      console.log(error);
      dispatch({
        type: types.DEPOSIT_MONEY_FAILURE,
        error: error.message,
      });
    }
  };

export const paymentHandler =
  ({ jwt, amount, paymentMethod }) =>
  async (dispatch) => {
    dispatch({ type: types.DEPOSIT_MONEY_REQUEST });

    try {
      const response = await api.post(
        `/api/payment/${paymentMethod}/amount/${amount}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );

      window.location.href = response.data.paymentUrl;

     
    } catch (error) {
      console.log(error);
      dispatch({
        type: types.DEPOSIT_MONEY_FAILURE,
        error: error.message,
      });
    }
  };


export const transferMoney =
  ({ jwt, walletId, reqData }) =>
  async (dispatch) => {
    dispatch({ type: types.TRANSFER_MONEY_REQUEST });

    try {
      const response = await api.put(
        `/api/wallet/${walletId}/transfer`,
        reqData,
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );

      dispatch({
        type: types.TRANSFER_MONEY_SUCCESS,
        payload: response.data,
      });
      console.log("transfermoney money sent", response.data);
    } catch (error) {
      console.log(error);
      dispatch({
        type: types.TRANSFER_MONEY_FAILURE,
        error: error.message,
      });
    }
  };

export const addTestWalletBalance =
  ({ jwt, amount }) =>
  async (dispatch) => {
    try {
      const response = await api.patch(
        `/api/wallet/test/add/amount/${amount}`,
        null,
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );

      dispatch({
        type: types.UPDATE_WALLET_BALANCE,
        payload: response.data,
      });
    } catch (error) {
      console.error("Failed to add test balance:", error);
    }
  };

export const withdrawalRequest =
  ({ jwt, amount }) =>
  async (dispatch) => {
    dispatch({ type: WITHDRAWAL_REQUEST });

    try {
      const response = await api.post(
        "/api/withdraw",
        { amount },
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );

      dispatch({
        type: WITHDRAWAL_SUCCESS,
        payload: response.data,
      });

      return response.data;
    } catch (error) {
      dispatch({
        type: WITHDRAWAL_FAILURE,
        error: error.message,
      });

      throw error;
    }
  };
