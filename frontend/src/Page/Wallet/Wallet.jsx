import React, { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  CopyIcon,
  DollarSign,
  DownloadIcon,
  ShuffleIcon,
  UploadIcon,
  WalletIcon,
} from "lucide-react";
import { ReloadIcon, UpdateIcon } from "@radix-ui/react-icons";
import TopUpForm from "./TopupForm";
import WithdrawalForm from "./WithdrawalForm";
import TransferForm from "./TransferForm";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useDispatch, useSelector } from "react-redux";
import {
  depositeMoney,
  getUserWallet,
  getWalletTransactions,
} from "@/State/Wallet/Action";
// import { useLocation } from "react-router-dom";
import { addTestWalletBalance } from "@/State/Wallet/Action";
import { Button } from "@/components/ui/button";

const Wallet = () => {
  const dispatch = useDispatch();
  const { wallet } = useSelector((store) => store);

  const handleFetchUserWallet = () => {
    dispatch(getUserWallet(localStorage.getItem("jwt")));
  };

  const handleAddTestAmount = () => {
    dispatch(
      addTestWalletBalance({
        jwt: localStorage.getItem("jwt"),
        amount: 5000,
      }),
    );

    dispatch(getUserWallet(localStorage.getItem("jwt")));
  };

  const handleFetchWalletTransaction = () => {
    dispatch(
      getWalletTransactions({
        jwt: localStorage.getItem("jwt"),
        walletId: wallet.userWallet?.id,
      }),
    );
  };

  useEffect(() => {
    const fetchWalletAndTransactions = async () => {
      const jwt = localStorage.getItem("jwt");
      const walletRes = await dispatch(getUserWallet(jwt));
      const walletId = walletRes?.id;

      if (walletId) {
        dispatch(getWalletTransactions({ jwt, walletId }));
      }
    };

    fetchWalletAndTransactions();
  }, []);
  console.log(
    "Wallet ID being used for transaction fetch:",
    wallet.userWallet?.id,
  );

  const location = useLocation();
  const navigate = useNavigate();
  useEffect(() => {
    const query = new URLSearchParams(location.search);
    const orderId = query.get("order_id");
    const paymentId = query.get("razorpay_payment_id");

    if (orderId && paymentId) {
      dispatch(
        depositeMoney({
          jwt: localStorage.getItem("jwt"),
          orderId,
          paymentId,
          navigate,
        }),
      );
    }
  }, [location.search]);

  return (
    <div className="flex flex-col items-center max-h-[600px] overflow-y-auto ">
      <div className="pt-10 w-full lg:w-[60%]">
        <Card>
          <CardHeader className="pb-9">
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-5">
                <WalletIcon size={35} className="" />
                <div>
                  <CardTitle className="text-2xl">My Wallet</CardTitle>
                  <div className="flex items-center gap-2">
                    <p className="text-gray-200 text-sm">
                      #{wallet.userWallet?.id}
                    </p>

                    <CopyIcon
                      size={20}
                      className="cursor-pointer hover:text-slate-300"
                    />
                  </div>
                </div>
              </div>
              <div>
                <ReloadIcon
                  onClick={handleFetchUserWallet}
                  className="w-6 h-6 cursor-pointer hover:text-gray-400"
                />
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="flex items-center">
              <DollarSign />
              <span className="text-2xl font-semibold">
                {wallet.userWallet?.balance ?? "0"}
              </span>
            </div>

            <div className="flex gap-7 mt-5">
              <Dialog>
                <DialogTrigger>
                  <div
                    className="h-25 w-25 hover:text-gray-400 cursor-pointer
                  flex flex-col items-center justify-center rounded-md shadow-slate-800 shadow-md"
                  >
                    <DownloadIcon />
                    <span className="text-sm mt-2">Add Money</span>
                  </div>
                </DialogTrigger>
                <DialogContent className="bg-slate-800">
                  <DialogHeader>
                    <DialogTitle>Top up your wallet</DialogTitle>
                  </DialogHeader>
                  <TopUpForm />
                </DialogContent>
              </Dialog>

              <Dialog>
                <DialogTrigger asChild>
                  <div
                    onClick={handleAddTestAmount}
                    className="h-25 w-25 hover:text-gray-400 cursor-pointer
      flex flex-col items-center justify-center rounded-md shadow-slate-800 shadow-md"
                  >
                    <DownloadIcon />
                    <span className="text-sm mt-2">Add ₹5000 Test</span>
                  </div>
                </DialogTrigger>
              </Dialog>

              <Dialog>
                <DialogTrigger>
                  <div
                    className="h-25 w-25 hover:text-gray-400 cursor-pointer 
                  flex flex-col items-center justify-center rounded-md shadow-slate-800 shadow-md"
                  >
                    <UploadIcon />
                    <span className="text-sm mt-2">Withdrawal</span>
                  </div>
                </DialogTrigger>
                <DialogContent className="bg-slate-800">
                  <DialogHeader>
                    <DialogTitle>Request withdrawal</DialogTitle>
                  </DialogHeader>
                  <WithdrawalForm />
                </DialogContent>
              </Dialog>

              <Dialog>
                <DialogTrigger>
                  <div
                    className="h-25 w-25 hover:text-gray-400 cursor-pointer
                  flex flex-col items-center justify-center rounded-md shadow-slate-800 shadow-md"
                  >
                    <ShuffleIcon />
                    <span className="text-sm mt-2"> Transfer</span>
                  </div>
                </DialogTrigger>
                <DialogContent className="bg-slate-800">
                  <DialogHeader>
                    <DialogTitle className="text-center text-xl">
                      Transfer to other wallet
                    </DialogTitle>
                  </DialogHeader>
                  <TransferForm />
                </DialogContent>
              </Dialog>
            </div>
          </CardContent>
        </Card>
        <div className="py-5 pt-10 ">
          <div className="flex gap-2 items-center pb-5">
            <h1 className="text-2xl font-semibold"> History</h1>
            <UpdateIcon className="h-5 w-5 cursor-pointer p-0 hover:text-gray-400" />
          </div>
          <Button
            onClick={() => {
              const jwt = localStorage.getItem("jwt");
              dispatch(
                getWalletTransactions({ jwt, walletId: wallet.userWallet?.id }),
              );
            }}
            className="my-4"
          >
            🔄 Load Transactions
          </Button>

          <div className=" space-y-3 pr-2">
            {Array.isArray(wallet.transactions) &&
              wallet.transactions.map((Item, i) => (
                <div key={i}>
                  <Card className="px-5 py-3">
                    <div className="flex items-center justify-between w-full">
                      <div className="flex items-center gap-4">
                        <Avatar onClick={handleFetchWalletTransaction}>
                          <AvatarFallback>
                            <ShuffleIcon className="w-5 h-5" />
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex flex-col">
                          <h1 className="font-medium leading-none">
                            {Item.type || Item.purpose}
                          </h1>
                          <p className="text-sm text-gray-500">{Item.date}</p>
                        </div>
                      </div>

                      <div>
                        <p
                          className={`text-base font-semibold ${
                            Number(Item.amount) < 0
                              ? "text-red-500"
                              : "text-green-500"
                          }`}
                        >
                          {Item.amount}
                        </p>
                      </div>
                    </div>
                  </Card>
                </div>
              ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Wallet;
